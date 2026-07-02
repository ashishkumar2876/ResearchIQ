import { useEffect, useState } from "react";
import {
  Upload,
  FileText,
  RefreshCw,
  Loader2,
  X,
  ExternalLink,
  Download,
  Brain,
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
  return paper?.paperId || paper?.id;
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

function DashboardPage() {
  const [analyses, setAnalyses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [selectedPaper, setSelectedPaper] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const formatError = (err) => {
    const data = err.response?.data;

    if (typeof data === "string") return data;
    if (data?.message) return data.message;
    if (data) return JSON.stringify(data);

    return err.message || "Something went wrong.";
  };

  const fetchAnalyses = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/analysis/dashboard");
      setAnalyses(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Dashboard fetch failed:", err);
      setError(formatError(err));
      setAnalyses([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalyses();
  }, []);

  const handleUpload = async (e) => {
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

      await fetchAnalyses();

      setTimeout(fetchAnalyses, 4000);
      setTimeout(fetchAnalyses, 8000);
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

  const confirmDelete = window.confirm(
    `Delete "${getPaperTitle(paper)}"?`
  );

  if (!confirmDelete) return;

  try {
    setError("");
    setMessage("");

    console.log("Deleting paperId:", paperId);

    await api.delete(`/papers/${paperId}`);

    setMessage("Paper deleted successfully.");

    setAnalyses((prev) =>
      prev.filter((item) => item.paperId !== paperId)
    );

    if (selectedPaper?.paperId === paperId) {
      setSelectedPaper(null);
    }

    setTimeout(fetchAnalyses, 1000);
  } catch (err) {
    console.error("Delete failed:", err);
    setError(formatError(err));
  }
};

  return (
    <AppLayout>
      <section className="rounded-3xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-blue-950/60 p-8 shadow-2xl">
        <div className="flex flex-col justify-between gap-6 lg:flex-row lg:items-center">
          <div>
            <p className="text-sm font-medium text-blue-400">
              Welcome to ResearchIQ
            </p>

            <h1 className="mt-3 text-4xl font-bold tracking-tight text-white">
              Analyze research papers with AI
            </h1>

            <p className="mt-3 max-w-2xl text-slate-400">
              Upload PDFs, generate AI analysis, and manage your research papers
              from one clean dashboard.
            </p>
          </div>

          <div>
            <input
              id="dashboard-upload"
              type="file"
              accept=".pdf,application/pdf"
              className="hidden"
              onChange={handleUpload}
              disabled={uploading}
            />

            <label
              htmlFor="dashboard-upload"
              className={`inline-flex cursor-pointer items-center gap-2 rounded-xl px-5 py-3 font-semibold text-white transition ${
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
        </div>
      </section>

      {message && (
        <div className="mt-5 rounded-xl border border-emerald-900/60 bg-emerald-950/40 px-4 py-3 text-sm text-emerald-300">
          {message}
        </div>
      )}

      {error && (
        <div className="mt-5 rounded-xl border border-red-900/60 bg-red-950/40 px-4 py-3 text-sm text-red-300">
          {error}
        </div>
      )}

      <section className="mt-8 grid gap-5 md:grid-cols-3">
        <StatCard
          icon={<FileText size={20} />}
          label="Analyzed Papers"
          value={analyses.length}
        />

        <StatCard
          icon={<Brain size={20} />}
          label="AI Status"
          value={analyses.length > 0 ? "Active" : "Ready"}
        />

        <StatCard
          icon={<Upload size={20} />}
          label="Upload"
          value={uploading ? "Running" : "Ready"}
        />
      </section>

      <section className="mt-8 rounded-3xl border border-slate-800 bg-slate-900/80 p-6">
        <div className="mb-5 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold text-white">
              Analyzed Papers
            </h2>

            <p className="mt-1 text-sm text-slate-400">
              {analyses.length} paper(s) available
            </p>
          </div>

          <button
            onClick={fetchAnalyses}
            disabled={loading}
            className="inline-flex items-center gap-2 rounded-xl border border-slate-700 px-4 py-2 text-sm text-slate-300 transition hover:bg-slate-800 disabled:opacity-60"
          >
            <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
            Refresh
          </button>
        </div>

        {loading ? (
          <div className="rounded-2xl border border-slate-800 bg-slate-950/70 p-8 text-center">
            <Loader2 className="mx-auto animate-spin text-blue-400" size={34} />
            <p className="mt-3 text-sm text-slate-400">
              Loading analyzed papers...
            </p>
          </div>
        ) : analyses.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-950/60 p-8 text-center">
            <FileText className="mx-auto text-slate-500" size={38} />
            <h3 className="mt-4 font-semibold text-white">
              No analyzed papers yet
            </h3>
            <p className="mt-2 text-sm text-slate-400">
              Upload a PDF from the dashboard to start analysis.
            </p>
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {analyses.map((paper) => (
              <DashboardPaperCard
                key={paper.id || paper.paperId}
                paper={paper}
                onOpen={() => setSelectedPaper(paper)}
                onDelete={() => handleDeletePaper(paper)}
              />
            ))}
          </div>
        )}
      </section>

      {selectedPaper && (
        <PaperDetailsModal
          paper={selectedPaper}
          onClose={() => setSelectedPaper(null)}
        />
      )}
    </AppLayout>
  );
}

function StatCard({ icon, label, value }) {
  return (
    <div className="rounded-2xl border border-slate-800 bg-slate-900/80 p-5">
      <div className="flex items-center gap-4">
        <div className="rounded-xl bg-blue-600/20 p-3 text-blue-400">
          {icon}
        </div>

        <div>
          <p className="text-sm text-slate-400">{label}</p>
          <p className="mt-1 text-xl font-bold text-white">{value}</p>
        </div>
      </div>
    </div>
  );
}

function DashboardPaperCard({ paper, onOpen, onDelete }) {
  const keywords = getKeywords(paper.keywords);

  return (
    <div
      onClick={onOpen}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter") onOpen();
      }}
      className="group cursor-pointer rounded-2xl border border-slate-800 bg-slate-950/70 p-5 transition hover:-translate-y-1 hover:border-blue-700/70 hover:bg-slate-950"
    >
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
        <span className="text-xs text-slate-500">Click to view details</span>

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
          <DetailSection title="Problem Statement" value={paper.problemStatement} />
          <DetailSection title="Methodology" value={paper.methodology} />
          <DetailSection title="Key Findings" value={paper.keyFindings} />
          <DetailSection title="Limitations" value={paper.limitations} />
          <DetailSection title="Research Gap" value={paper.researchGap} />
          <DetailSection title="Future Scope" value={paper.futureScope || paper.futureWork} />
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

export default DashboardPage;