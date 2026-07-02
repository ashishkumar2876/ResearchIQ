import { NavLink, useNavigate } from "react-router-dom";
import {
  BookOpen,
  LayoutDashboard,
  Search,
  FileText,
  Brain,
  GitCompare,
  Library,
  LogOut,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const navClass = ({ isActive }) =>
    `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
      isActive
        ? "bg-blue-600 text-white shadow-lg shadow-blue-600/20"
        : "text-slate-400 hover:bg-slate-800/80 hover:text-white"
    }`;

  return (
    <aside className="fixed left-0 top-0 h-screen w-72 border-r border-slate-800 bg-slate-950 px-4 py-6">
      <div className="mb-10 flex items-center gap-3 px-2">
        <div className="rounded-xl bg-blue-600 p-2">
          <BookOpen size={24} />
        </div>
        <h1 className="text-xl font-bold text-white">
          Research<span className="text-blue-400">IQ</span>
        </h1>
      </div>

      <nav className="space-y-2">
        <NavLink to="/dashboard" className={navClass}>
          <LayoutDashboard size={19} />
          Dashboard
        </NavLink>

        <NavLink to="/research" className={navClass}>
          <Search size={19} />
          Research Center
        </NavLink>
      </nav>

      <div className="mt-8 border-t border-slate-800 pt-6">
        <p className="mb-3 px-4 text-xs font-semibold uppercase tracking-wider text-slate-500">
          Tools
        </p>

        <div className="space-y-2">
          <div className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm text-slate-400">
            <FileText size={18} /> My Papers
          </div>
          <div className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm text-slate-400">
            <Brain size={18} /> AI Analysis
          </div>
          <div className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm text-slate-400">
            <GitCompare size={18} /> Comparisons
          </div>
          <div className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm text-slate-400">
            <Library size={18} /> Literature Review
          </div>
        </div>
      </div>

      <button
        onClick={handleLogout}
        className="absolute bottom-6 left-4 right-4 flex items-center justify-center gap-2 rounded-xl border border-red-900/60 px-4 py-3 text-sm font-medium text-red-300 transition hover:bg-red-950/40"
      >
        <LogOut size={18} />
        Logout
      </button>
    </aside>
  );
}

export default Navbar;