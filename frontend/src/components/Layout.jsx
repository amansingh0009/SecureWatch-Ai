import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { Activity, Bug, FileText, Gauge, LogOut, Radar, Shield, Skull, User, Users } from 'lucide-react';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';

const links = [
  ['Dashboard', '/dashboard', Gauge],
  ['Website', '/scanner/website', Shield],
  ['Ports', '/scanner/ports', Radar],
  ['Malware', '/scanner/malware', Skull],
  ['Reports', '/reports', FileText],
  ['Profile', '/profile', User],
  ['Admin', '/admin', Users]
];

export default function Layout() {
  const { logout, isAdmin, user } = useAuth();
  const navigate = useNavigate();
  return (
    <div className="min-h-screen lg:grid lg:grid-cols-[260px_1fr]">
      <aside className="glass sticky top-0 z-20 h-auto border-b border-line/70 p-4 lg:h-screen lg:border-b-0 lg:border-r">
        <div className="mb-6 flex items-center gap-3">
          <div className="grid h-10 w-10 place-items-center rounded bg-neon/15 text-neon"><Activity size={22} /></div>
          <div>
            <p className="text-lg font-semibold">SecureWatch AI</p>
            <p className="text-xs text-slate-400">Threat command center</p>
          </div>
        </div>
        <nav className="grid gap-2">
          {links.filter(([label]) => label !== 'Admin' || isAdmin).map(([label, path, Icon]) => (
            <NavLink
              key={path}
              to={path}
              className={({ isActive }) => `flex items-center gap-3 rounded px-3 py-2 text-sm transition ${isActive ? 'bg-neon/15 text-neon' : 'text-slate-300 hover:bg-white/5'}`}
            >
              <Icon size={18} /> {label}
            </NavLink>
          ))}
        </nav>
        <button
          onClick={() => { logout(); navigate('/'); }}
          className="mt-6 flex w-full items-center gap-3 rounded border border-line px-3 py-2 text-sm text-slate-300 hover:bg-white/5"
        >
          <LogOut size={18} /> Logout
        </button>
        <p className="mt-6 text-xs text-slate-500">{user?.email}</p>
      </aside>
      <main className="p-4 md:p-8">
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.28 }}>
          <Outlet />
        </motion.div>
      </main>
    </div>
  );
}
