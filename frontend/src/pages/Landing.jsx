import { Link } from 'react-router-dom';
import { ArrowRight, Bot, Radar, ShieldCheck } from 'lucide-react';
import { motion } from 'framer-motion';

export default function Landing() {
  return (
    <main className="min-h-screen">
      <section className="mx-auto grid min-h-screen max-w-6xl content-center gap-10 px-6 py-10 lg:grid-cols-[1.1fr_.9fr] lg:items-center">
        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}>
          <div className="mb-5 inline-flex items-center gap-2 rounded border border-neon/30 bg-neon/10 px-3 py-1 text-sm text-neon">
            <ShieldCheck size={16} /> AI-powered security monitoring
          </div>
          <h1 className="text-5xl font-bold tracking-normal text-white md:text-7xl">SecureWatch AI</h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
            Scan websites, inspect exposed services, check malware hashes, track suspicious activity, and turn findings into practical remediation guidance.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link to="/register" className="inline-flex items-center gap-2 rounded bg-neon px-5 py-3 font-semibold text-ink">
              Start monitoring <ArrowRight size={18} />
            </Link>
            <Link to="/login" className="rounded border border-line px-5 py-3 font-semibold text-slate-200 hover:bg-white/5">Login</Link>
          </div>
        </motion.div>
        <motion.div initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} className="glass rounded p-6 shadow-glow">
          <div className="mb-6 flex items-center justify-between">
            <p className="font-semibold text-slate-100">Threat Radar</p>
            <Radar className="text-cyan" />
          </div>
          <div className="grid gap-3">
            {[
              ['Reflected XSS pattern', 'HIGH', 'Input sanitizer missing'],
              ['Open MySQL port', 'HIGH', 'Restrict ingress'],
              ['Missing CSP header', 'MEDIUM', 'Add policy'],
              ['AI guidance ready', 'LOW', 'Remediation generated']
            ].map(([name, severity, text]) => (
              <div key={name} className="flex items-center justify-between rounded border border-line bg-ink/70 p-4">
                <div>
                  <p className="font-medium">{name}</p>
                  <p className="text-sm text-slate-500">{text}</p>
                </div>
                <span className={`severity-${severity} rounded px-2 py-1 text-xs font-semibold`}>{severity}</span>
              </div>
            ))}
          </div>
          <div className="mt-6 flex items-center gap-3 text-sm text-slate-400"><Bot className="text-neon" size={18} /> AI summaries convert noisy scanner output into action.</div>
        </motion.div>
      </section>
    </main>
  );
}
