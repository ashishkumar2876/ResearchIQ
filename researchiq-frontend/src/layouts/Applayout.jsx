import Navbar from "../components/Navbar";

function AppLayout({ children }) {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <Navbar />
      <main className="ml-72 min-h-screen px-8 py-8">{children}</main>
    </div>
  );
}

export default AppLayout;