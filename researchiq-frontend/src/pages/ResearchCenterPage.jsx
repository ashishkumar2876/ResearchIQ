import { useEffect, useMemo, useState } from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import {
  Upload,
  Search,
  FileText,
  Brain,
  GitCompare,
  Library,
  Lightbulb,
  Loader2,
  RefreshCw,
  X,
  ExternalLink,
  Download,
  Trash2,
} from "lucide-react";
import AppLayout from "../layouts/AppLayout";
import api from "../api/axios";

function safeText(value, fallback = "") {
  if (value === null || value === undefined || value === "") return fallback;

  if (
    typeof value === "string" ||
    typeof value === "number" ||
    typeof value === "boolean"
  ) {
    return String(value);
  }

  return JSON.stringify(value);
}

function getKeywords(value) {
  if (Array.isArray(value)) return value;

  if (typeof value === "string") {
    return value
      .split(",")
      .map((item) => item.trim())
      .filter(Boolean);
  }

  return [];
}

function getPaperTitle(paper) {
  return safeText(
    paper?.title || paper?.fileName || paper?.name,
    `Paper #${safeText(paper?.paperId || paper?.id, "N/A")}`
  );
}

function getPaperId(paper) {
  return paper?.paperId;
}

function getPdfUrl(paper) {
  return (
    paper?.pdfUrl ||
    paper?.fileUrl ||
    paper?.cloudinaryUrl ||
    paper?.secureUrl ||
    paper?.url ||
    paper?.paperUrl ||
    ""
  );
}

function getDownloadFileName(title) {
  return `${safeText(title, "research-paper")
    .replace(/[<>:"/\\|?*]+/g, "")
    .slice(0, 80)}.pdf`;
}

function ResearchCenterPage() {
  const [uploading, setUploading] = useState(false);
  const [loadingPapers, setLoadingPapers] = useState(false);
  const [papers, setPapers] = useState([]);
  const [selectedPaperIds, setSelectedPaperIds] = useState([]);
  const [selectedPaper, setSelectedPaper] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [actionLoading, setActionLoading] = useState(false);
  const [resultTitle, setResultTitle] = useState("");
  const [resultContent, setResultContent] = useState("");
  const [resultModalOpen, setResultModalOpen] = useState(false);

  const formatError = (err) => {
    const data = err.response?.data;

    if (typeof data === "string") return data;
    if (data?.message) return data.message;
    if (data) return JSON.stringify(data);

    return err.message || "Something went wrong.";
  };

  const getMarkdownText = (data) => {
    if (!data) return "";

    let text = "";

    if (typeof data === "string") {
      text = data;
    } else {
      text =
        data.markdown ||
        data.content ||
        data.response ||
        data.result ||
        data.text ||
        data.message ||
        JSON.stringify(data, null, 2);
    }

    return String(text)
      .replace(/\\n/g, "\n")
      .replace(/\\"/g, '"')
      .trim();
  };

  const fetchPapers = async () => {
    try {
      setLoadingPapers(true);
      setError("");

      const response = await api.get("/analysis/dashboard");
      setPapers(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Failed to fetch papers:", err);
      setError(formatError(err));
      setPapers([]);
    } finally {
      setLoadingPapers(false);
    }
  };

  useEffect(() => {
    fetchPapers();
  }, []);

  const handleFileChange = async (e) => {
    const file = e.target.files?.[0];

    if (!file) return;

    setMessage("");
    setError("");

    const isPdf =
      file.type === "application/pdf" ||
      file.name.toLowerCase().endsWith(".pdf");

    if (!isPdf) {
      setError("Only PDF files are allowed.");
      e.target.value = "";
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setUploading(true);

      await api.post("/papers/upload", formData);

      setMessage(
        "Paper uploaded successfully. AI analysis may take a few seconds."
      );

      await fetchPapers();

      setTimeout(fetchPapers, 4000);
      setTimeout(fetchPapers, 8000);
    } catch (err) {
      console.error("Upload failed:", err);
      setError(formatError(err));
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  const handleDeletePaper = async (paper) => {
    const paperId = paper.paperId;

    if (!paperId) {
      alert("Paper ID not found. Cannot delete this paper.");
      console.log("Paper object:", paper);
      return;
    }

    const confirmDelete = window.confirm(`Delete "${getPaperTitle(paper)}"?`);

    if (!confirmDelete) return;

    try {
      setError("");
      setMessage("");

      await api.delete(`/papers/${paperId}`);

      setMessage("Paper deleted successfully.");

      setPapers((prev) => prev.filter((item) => item.paperId !== paperId));

      setSelectedPaperIds((prev) => prev.filter((id) => id !== paperId));

      if (selectedPaper?.paperId === paperId) {
        setSelectedPaper(null);
      }

      setTimeout(fetchPapers, 1000);
    } catch (err) {
      console.error("Delete failed:", err);
      setError(formatError(err));
    }
  };

  const handleViewInsights = async () => {
    if (selectedPaperIds.length !== 1) {
      setError("Select exactly one paper to view insights.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");
      setResultTitle("Paper Insights");
      setResultContent("");
      setResultModalOpen(true);

      const response = await api.get(`/insights/paper/${selectedPaperIds[0]}`);

      setResultContent(getMarkdownText(response.data));
    } catch (err) {
      console.error("Insights failed:", err);
      setError(formatError(err));
      setResultModalOpen(false);
    } finally {
      setActionLoading(false);
    }
  };

  const handleComparePapers = async () => {
    if (selectedPaperIds.length < 2) {
      setError("Select at least two papers to compare.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");
      setResultTitle("Paper Comparison");
      setResultContent("");
      setResultModalOpen(true);

      const response = await api.post("/insights/compare", {
        paperIds: selectedPaperIds,
      });

      setResultContent(getMarkdownText(response.data));
    } catch (err) {
      console.error("Compare failed:", err);
      setError(formatError(err));
      setResultModalOpen(false);
    } finally {
      setActionLoading(false);
    }
  };

  const handleResearchGap = async () => {
    if (selectedPaperIds.length === 0) {
      setError("Select at least one paper to discover research gaps.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");
      setResultTitle("Research Gap Analysis");
      setResultContent("");
      setResultModalOpen(true);

      const response = await api.post("/insights/research-gap", {
        paperIds: selectedPaperIds,
      });

      setResultContent(getMarkdownText(response.data));
    } catch (err) {
      console.error("Research gap failed:", err);
      setError(formatError(err));
      setResultModalOpen(false);
    } finally {
      setActionLoading(false);
    }
  };

  const handleLiteratureReview = async () => {
    if (selectedPaperIds.length === 0) {
      setError("Select at least one paper to generate literature review.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");
      setResultTitle("Literature Review");
      setResultContent("");
      setResultModalOpen(true);

      const response = await api.post("/insights/literature-review", {
        paperIds: selectedPaperIds,
      });

      setResultContent(getMarkdownText(response.data));
    } catch (err) {
      console.error("Literature review failed:", err);
      setError(formatError(err));
      setResultModalOpen(false);
    } finally {
      setActionLoading(false);
    }
  };

  const filteredPapers = useMemo(() => {
    return papers.filter((paper) =>
      getPaperTitle(paper).toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [papers, searchTerm]);

  const togglePaperSelection = (paperId) => {
    if (!paperId) return;

    setSelectedPaperIds((prev) =>
      prev.includes(paperId)
        ? prev.filter((id) => id !== paperId)
        : [...prev, paperId]
    );
  };

  return (
    <AppLayout>
      <section className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <p className="text-sm font-medium text-blue-400">Research Center</p>

          <h1 className="mt-2 text-4xl font-bold tracking-tight text-white">
            Your paper workspace
          </h1>

          <p className="mt-3 max-w-2xl text-slate-400">
            Select analyzed papers and generate insights, comparisons, research
            gaps, and literature reviews.
          </p>
        </div>

        <div>
          <input
            id="paper-upload"
            type="file"
            accept=".pdf,application/pdf"
            className="hidden"
            onChange={handleFileChange}
            disabled={uploading}
          />

          <label
            htmlFor="paper-upload"
            className={`inline-flex cursor-pointer items-center justify-center gap-2 rounded-xl px-5 py-3 font-semibold text-white transition ${
              uploading
                ? "cursor-not-allowed bg-blue-600/60 opacity-70"
                : "bg-blue-600 hover:bg-blue-500"
            }`}
          >
            {uploading ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Uploading...
              </>
            ) : (
              <>
                <Upload size={18} />
                Upload Paper
              </>
            )}
          </label>
        </div>
      </section>

      {message && (
        <div className="mb-5 rounded-xl border border-emerald-900/60 bg-emerald-950/40 px-4 py-3 text-sm text-emerald-300">
          {message}
        </div>
      )}

      {error && (
        <div className="mb-5 rounded-xl border border-red-900/60 bg-red-950/40 px-4 py-3 text-sm text-red-300">
          {error}
        </div>
      )}

      <section className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
        <div className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6">
          <div className="mb-5 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">My Papers</h2>

              <p className="mt-1 text-sm text-slate-400">
                {selectedPaperIds.length > 0
                  ? `${selectedPaperIds.length} selected`
                  : `${papers.length} paper(s) available`}
              </p>
            </div>

            <div className="flex gap-3">
              <div className="relative">
                <Search
                  size={17}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
                />

                <input
                  type="text"
                  placeholder="Search papers..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full rounded-xl border border-slate-700 bg-slate-950 py-2.5 pl-10 pr-4 text-sm text-white outline-none focus:border-blue-500 md:w-64"
                />
              </div>

              <button
                onClick={fetchPapers}
                disabled={loadingPapers}
                className="inline-flex items-center gap-2 rounded-xl border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-300 transition hover:bg-slate-800 disabled:opacity-60"
              >
                <RefreshCw
                  size={16}
                  className={loadingPapers ? "animate-spin" : ""}
                />
              </button>
            </div>
          </div>

          <div className="grid gap-4">
            {loadingPapers ? (
              <div className="rounded-2xl border border-slate-800 bg-slate-950/70 p-8 text-center">
                <Loader2
                  className="mx-auto animate-spin text-blue-400"
                  size={34}
                />

                <p className="mt-3 text-sm text-slate-400">
                  Loading papers...
                </p>
              </div>
            ) : filteredPapers.length === 0 ? (
              <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-950/60 p-8 text-center">
                <FileText className="mx-auto text-slate-500" size={36} />

                <h3 className="mt-4 font-semibold text-white">
                  No papers found
                </h3>

                <p className="mt-2 text-sm text-slate-400">
                  Upload a PDF. After analysis completes, it will appear here.
                </p>
              </div>
            ) : (
              filteredPapers.map((paper) => {
                const paperId = getPaperId(paper);

                return (
                  <ResearchPaperCard
                    key={paper.id || paper.paperId}
                    paper={paper}
                    selected={selectedPaperIds.includes(paperId)}
                    onToggle={() => togglePaperSelection(paperId)}
                    onOpen={() => setSelectedPaper(paper)}
                    onDelete={() => handleDeletePaper(paper)}
                  />
                );
              })
            )}
          </div>
        </div>

        <div className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6">
          <h2 className="text-xl font-semibold text-white">AI Actions</h2>

          <p className="mt-1 text-sm text-slate-400">
            Select papers to enable research actions.
          </p>

          <div className="mt-6 grid gap-4">
            <ActionCard
              icon={<Brain size={20} />}
              title="View Insights"
              desc="Generate detailed insights for one selected paper."
              disabled={selectedPaperIds.length !== 1 || actionLoading}
              onClick={handleViewInsights}
            />

            <ActionCard
              icon={<GitCompare size={20} />}
              title="Compare Papers"
              desc="Compare two or more selected papers."
              disabled={selectedPaperIds.length < 2 || actionLoading}
              onClick={handleComparePapers}
            />

            <ActionCard
              icon={<Lightbulb size={20} />}
              title="Research Gap"
              desc="Discover research gaps from selected papers."
              disabled={selectedPaperIds.length === 0 || actionLoading}
              onClick={handleResearchGap}
            />

            <ActionCard
              icon={<Library size={20} />}
              title="Literature Review"
              desc="Generate literature review from selected papers."
              disabled={selectedPaperIds.length === 0 || actionLoading}
              onClick={handleLiteratureReview}
            />
          </div>

          <div className="mt-6 rounded-2xl border border-dashed border-slate-700 bg-slate-950/60 p-5">
            <div className="flex items-center justify-between gap-3">
              <h3 className="font-semibold text-white">Workspace</h3>

              {actionLoading && (
                <Loader2 size={18} className="animate-spin text-blue-400" />
              )}
            </div>

            <p className="mt-2 text-sm leading-6 text-slate-400">
              Select papers and choose an AI action. Results will open in a
              popup.
            </p>

            {resultContent && !actionLoading && (
              <button
                type="button"
                onClick={() => setResultModalOpen(true)}
                className="mt-4 rounded-xl border border-blue-800 px-4 py-2 text-sm font-semibold text-blue-300 transition hover:bg-blue-950/50"
              >
                Open last result
              </button>
            )}
          </div>
        </div>
      </section>

      {selectedPaper && (
        <PaperDetailsModal
          paper={selectedPaper}
          onClose={() => setSelectedPaper(null)}
        />
      )}

      {resultModalOpen && (
        <InsightResultModal
          title={resultTitle}
          content={resultContent}
          loading={actionLoading}
          onClose={() => setResultModalOpen(false)}
        />
      )}
    </AppLayout>
  );
}

function ResearchPaperCard({ paper, selected, onToggle, onOpen, onDelete }) {
  const keywords = getKeywords(paper.keywords);

  return (
    <div
      onClick={onOpen}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter") onOpen();
      }}
      className={`cursor-pointer rounded-2xl border p-5 transition hover:-translate-y-1 hover:border-blue-700/70 ${
        selected
          ? "border-blue-700 bg-blue-950/30"
          : "border-slate-800 bg-slate-950/70 hover:bg-slate-950"
      }`}
    >
      <div className="flex items-start gap-4">
        <input
          type="checkbox"
          checked={selected}
          onChange={onToggle}
          onClick={(e) => e.stopPropagation()}
          className="mt-1 h-4 w-4 shrink-0 accent-blue-600"
        />

        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-3">
            <div className="flex min-w-0 items-start gap-3">
              <div className="rounded-xl bg-blue-600/20 p-2 text-blue-400">
                <FileText size={18} />
              </div>

              <div className="min-w-0">
                <h3 className="line-clamp-2 font-semibold text-white">
                  {getPaperTitle(paper)}
                </h3>

                <p className="mt-1 text-xs text-slate-500">
                  Paper #{safeText(getPaperId(paper), "N/A")}
                </p>
              </div>
            </div>

            {paper.noveltyScore !== undefined && paper.noveltyScore !== null && (
              <span className="shrink-0 rounded-full border border-blue-900/60 bg-blue-950/50 px-2.5 py-1 text-xs text-blue-300">
                {safeText(paper.noveltyScore)}/10
              </span>
            )}
          </div>

          <p className="mt-4 line-clamp-2 text-sm leading-6 text-slate-400">
            {safeText(paper.summary, "Summary unavailable.")}
          </p>

          {keywords.length > 0 && (
            <div className="mt-4 flex flex-wrap gap-2">
              {keywords.slice(0, 3).map((keyword) => (
                <span
                  key={keyword}
                  className="rounded-full bg-slate-800 px-2.5 py-1 text-xs text-slate-300"
                >
                  {keyword}
                </span>
              ))}
            </div>
          )}

          <div className="mt-5 flex items-center justify-between border-t border-slate-800 pt-4">
            <span className="text-xs text-slate-500">
              Click to view details
            </span>

            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                onDelete();
              }}
              className="inline-flex items-center gap-1 rounded-lg px-2 py-1 text-xs text-red-400 transition hover:bg-red-950/50 hover:text-red-300"
            >
              <Trash2 size={14} />
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function ActionCard({ icon, title, desc, disabled, onClick }) {
  return (
    <button
      type="button"
      disabled={disabled}
      onClick={onClick}
      className={`flex w-full items-start gap-4 rounded-2xl border p-4 text-left transition ${
        disabled
          ? "cursor-not-allowed border-slate-800 bg-slate-950/40 opacity-50"
          : "border-slate-800 bg-slate-950/70 hover:border-blue-800 hover:bg-slate-950"
      }`}
    >
      <div className="rounded-xl bg-blue-600/20 p-3 text-blue-400">
        {icon}
      </div>

      <div>
        <h3 className="font-semibold text-white">{title}</h3>
        <p className="mt-1 text-sm text-slate-400">{desc}</p>
      </div>
    </button>
  );
}

function InsightResultModal({ title, content, loading, onClose }) {
  const copyResult = async () => {
    try {
      await navigator.clipboard.writeText(content || "");
      alert("Result copied.");
    } catch (err) {
      console.error("Copy failed:", err);
      alert("Unable to copy result.");
    }
  };

  const downloadResult = () => {
    const blob = new Blob([content || ""], {
      type: "text/markdown;charset=utf-8",
    });

    const url = window.URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = `${safeText(title, "research-result")
      .replace(/[<>:"/\\|?*]+/g, "")
      .slice(0, 80)}.md`;

    document.body.appendChild(link);
    link.click();

    link.remove();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 px-4 py-8">
      <div className="flex max-h-[92vh] w-full max-w-7xl flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950 shadow-2xl">
        <div className="shrink-0 flex items-start justify-between gap-4 border-b border-slate-800 p-6">
          <div>
            <p className="text-sm font-medium text-blue-400">AI Result</p>

            <h2 className="mt-2 text-2xl font-bold text-white">
              {safeText(title, "Generated Result")}
            </h2>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-xl border border-slate-700 p-2 text-slate-400 transition hover:bg-slate-800 hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        <div className="min-h-0 flex-1 overflow-y-auto p-6">
          <div className="mb-5 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={copyResult}
              disabled={!content}
              className="rounded-xl border border-slate-700 px-4 py-2 text-sm font-semibold text-slate-300 transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Copy
            </button>

            <button
              type="button"
              onClick={downloadResult}
              disabled={!content}
              className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Download Markdown
            </button>
          </div>

          {loading ? (
            <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-10 text-center">
              <Loader2
                className="mx-auto animate-spin text-blue-400"
                size={34}
              />

              <p className="mt-3 text-sm text-slate-400">
                Generating result...
              </p>
            </div>
          ) : (
            <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                components={{
                  h1: ({ children }) => (
                    <h1 className="mb-5 mt-2 text-3xl font-bold text-white">
                      {children}
                    </h1>
                  ),

                  h2: ({ children }) => (
                    <h2 className="mb-4 mt-8 border-b border-slate-800 pb-2 text-2xl font-bold text-white">
                      {children}
                    </h2>
                  ),

                  h3: ({ children }) => (
                    <h3 className="mb-3 mt-6 text-xl font-semibold text-white">
                      {children}
                    </h3>
                  ),

                  p: ({ children }) => (
                    <p className="mb-4 text-sm leading-7 text-slate-300">
                      {children}
                    </p>
                  ),

                  strong: ({ children }) => (
                    <strong className="font-semibold text-white">
                      {children}
                    </strong>
                  ),

                  ul: ({ children }) => (
                    <ul className="mb-5 ml-6 list-disc space-y-2 text-sm leading-7 text-slate-300">
                      {children}
                    </ul>
                  ),

                  ol: ({ children }) => (
                    <ol className="mb-5 ml-6 list-decimal space-y-2 text-sm leading-7 text-slate-300">
                      {children}
                    </ol>
                  ),

                  li: ({ children }) => (
                    <li className="pl-1 text-slate-300">{children}</li>
                  ),

                  table: ({ children }) => (
                    <div className="my-6 w-full overflow-x-auto rounded-2xl border border-slate-700">
                      <table className="min-w-[1250px] table-fixed border-collapse text-left text-sm">
                        {children}
                      </table>
                    </div>
                  ),

                  thead: ({ children }) => (
                    <thead className="bg-slate-800 text-slate-100">
                      {children}
                    </thead>
                  ),

                  tbody: ({ children }) => (
                    <tbody className="divide-y divide-slate-800 bg-slate-950/70">
                      {children}
                    </tbody>
                  ),

                  tr: ({ children }) => (
                    <tr className="align-top">{children}</tr>
                  ),

                  th: ({ children }) => (
                    <th className="w-[380px] border-r border-slate-700 px-4 py-3 align-top font-semibold leading-6 text-white last:border-r-0">
                      {children}
                    </th>
                  ),

                  td: ({ children }) => (
                    <td className="w-[380px] break-words border-r border-slate-800 px-4 py-3 align-top text-sm leading-7 text-slate-300 last:border-r-0 [&_p]:mb-0 [&_ul]:mb-0 [&_ol]:mb-0">
                      {children}
                    </td>
                  ),

                  code: ({ children }) => (
                    <code className="rounded bg-slate-800 px-1.5 py-0.5 text-xs text-blue-300">
                      {children}
                    </code>
                  ),

                  blockquote: ({ children }) => (
                    <blockquote className="my-4 border-l-4 border-blue-600 pl-4 text-slate-400">
                      {children}
                    </blockquote>
                  ),
                }}
              >
                {safeText(content, "No result available.")}
              </ReactMarkdown>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function PaperDetailsModal({ paper, onClose }) {
  const keywords = getKeywords(paper.keywords);
  const pdfUrl = getPdfUrl(paper);

  const openPdf = () => {
    if (!pdfUrl) {
      alert("PDF URL not available for this paper.");
      return;
    }

    window.open(pdfUrl, "_blank", "noopener,noreferrer");
  };

  const downloadPdf = async () => {
    if (!pdfUrl) {
      alert("PDF URL not available for this paper.");
      return;
    }

    try {
      const response = await fetch(pdfUrl);

      if (!response.ok) {
        throw new Error("PDF download failed");
      }

      const blob = await response.blob();
      const blobUrl = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = blobUrl;
      link.download = getDownloadFileName(getPaperTitle(paper));

      document.body.appendChild(link);
      link.click();

      link.remove();
      window.URL.revokeObjectURL(blobUrl);
    } catch (err) {
      console.error("Download failed, opening PDF instead:", err);
      window.open(pdfUrl, "_blank", "noopener,noreferrer");
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4 py-8">
      <div className="max-h-[90vh] w-full max-w-5xl overflow-hidden rounded-3xl border border-slate-800 bg-slate-950 shadow-2xl">
        <div className="flex items-start justify-between gap-4 border-b border-slate-800 p-6">
          <div>
            <p className="text-sm font-medium text-blue-400">
              Paper #{safeText(getPaperId(paper), "N/A")}
            </p>

            <h2 className="mt-2 text-2xl font-bold text-white">
              {getPaperTitle(paper)}
            </h2>

            <p className="mt-2 text-sm text-slate-500">
              {safeText(paper.researchDomain, "Research domain unavailable")}
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-xl border border-slate-700 p-2 text-slate-400 transition hover:bg-slate-800 hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        <div className="max-h-[calc(90vh-105px)] overflow-y-auto p-6">
          <div className="mb-6 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={openPdf}
              className="inline-flex items-center gap-2 rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-500"
            >
              <ExternalLink size={16} />
              Open PDF
            </button>

            <button
              type="button"
              onClick={downloadPdf}
              className="inline-flex items-center gap-2 rounded-xl border border-slate-700 px-4 py-2 text-sm font-semibold text-slate-300 transition hover:bg-slate-800"
            >
              <Download size={16} />
              Download PDF
            </button>
          </div>

          <div className="grid gap-5 lg:grid-cols-3">
            <InfoBox
              title="Novelty Score"
              value={
                paper.noveltyScore !== undefined && paper.noveltyScore !== null
                  ? `${safeText(paper.noveltyScore)}/10`
                  : "N/A"
              }
            />

            <InfoBox
              title="Difficulty"
              value={safeText(paper.difficulty, "N/A")}
            />

            <InfoBox
              title="Domain"
              value={safeText(paper.researchDomain, "N/A")}
            />
          </div>

          {keywords.length > 0 && (
            <section className="mt-6 rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
              <h3 className="font-semibold text-white">Keywords</h3>

              <div className="mt-3 flex flex-wrap gap-2">
                {keywords.map((keyword) => (
                  <span
                    key={keyword}
                    className="rounded-full bg-blue-950/60 px-3 py-1 text-xs text-blue-300"
                  >
                    {keyword}
                  </span>
                ))}
              </div>
            </section>
          )}

          <DetailSection title="Summary" value={paper.summary} />
          <DetailSection
            title="Problem Statement"
            value={paper.problemStatement}
          />
          <DetailSection title="Methodology" value={paper.methodology} />
          <DetailSection title="Key Findings" value={paper.keyFindings} />
          <DetailSection title="Limitations" value={paper.limitations} />
          <DetailSection title="Research Gap" value={paper.researchGap} />
          <DetailSection
            title="Future Scope"
            value={paper.futureScope || paper.futureWork}
          />
          <DetailSection title="Conclusion" value={paper.conclusion} />

          <section className="mt-6 rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
            <h3 className="font-semibold text-white">Raw Analysis Data</h3>

            <pre className="mt-4 max-h-80 overflow-auto rounded-xl bg-slate-950 p-4 text-xs leading-6 text-slate-400">
              {JSON.stringify(paper, null, 2)}
            </pre>
          </section>
        </div>
      </div>
    </div>
  );
}

function InfoBox({ title, value }) {
  return (
    <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
      <p className="text-xs uppercase tracking-wider text-slate-500">
        {title}
      </p>

      <p className="mt-2 text-lg font-semibold text-white">{value}</p>
    </div>
  );
}

function DetailSection({ title, value }) {
  const text = safeText(value, "");

  if (!text) return null;

  return (
    <section className="mt-6 rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
      <h3 className="font-semibold text-white">{title}</h3>

      <p className="mt-3 whitespace-pre-line text-sm leading-7 text-slate-400">
        {text}
      </p>
    </section>
  );
}

export default ResearchCenterPage;