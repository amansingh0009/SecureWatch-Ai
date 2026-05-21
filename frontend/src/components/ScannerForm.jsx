import { Loader2, Search } from 'lucide-react';

export default function ScannerForm({ title, placeholder, button, onSubmit, loading }) {
  return (
    <form onSubmit={onSubmit} className="glass mb-6 rounded p-5">
      <label className="mb-2 block text-sm text-slate-300">{title}</label>
      <div className="grid gap-3 md:grid-cols-[1fr_auto]">
        <input
          name="target"
          required
          placeholder={placeholder}
          className="rounded border border-line bg-ink px-4 py-3 text-slate-100 outline-none focus:border-neon"
        />
        <button disabled={loading} className="inline-flex items-center justify-center gap-2 rounded bg-neon px-5 py-3 font-semibold text-ink disabled:opacity-60">
          {loading ? <Loader2 className="animate-spin" size={18} /> : <Search size={18} />} {button}
        </button>
      </div>
    </form>
  );
}
