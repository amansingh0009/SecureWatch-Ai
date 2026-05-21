export default function StatCard({ title, value, tone = 'neon', detail }) {
  const tones = {
    neon: 'text-neon',
    cyan: 'text-cyan',
    amber: 'text-amber',
    danger: 'text-danger'
  };
  return (
    <div className="glass rounded p-5">
      <p className="text-sm text-slate-400">{title}</p>
      <p className={`mt-3 text-3xl font-semibold ${tones[tone]}`}>{value}</p>
      {detail && <p className="mt-2 text-xs text-slate-500">{detail}</p>}
    </div>
  );
}
