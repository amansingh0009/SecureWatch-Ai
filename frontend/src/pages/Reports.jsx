import { useEffect, useState } from 'react';
import { Download, FilePlus } from 'lucide-react';
import api from '../api/client';

export default function Reports() {
  const [reports, setReports] = useState([]);
  const [scans, setScans] = useState([]);
  const load = async () => {
    const [reportRes, scanRes] = await Promise.all([api.get('/reports'), api.get('/scans')]);
    setReports(reportRes.data);
    setScans(scanRes.data);
  };
  useEffect(() => { load(); }, []);
  const create = async (scanId) => {
    await api.post(`/reports/scan/${scanId}`);
    load();
  };
  return (
    <div>
      <h1 className="text-3xl font-semibold">Reports</h1>
      <p className="mt-2 text-slate-400">Generate threat summaries from completed scans and export them.</p>
      <div className="mt-6 grid gap-4 xl:grid-cols-2">
        <div className="glass rounded p-5">
          <h2 className="font-semibold">Create report from scan</h2>
          <div className="mt-4 grid gap-3">
            {scans.map((scan) => (
              <button key={scan.id} onClick={() => create(scan.id)} className="flex items-center justify-between rounded border border-line bg-ink p-3 text-left hover:border-neon">
                <span>{scan.type}: {scan.target}</span>
                <FilePlus size={18} className="text-neon" />
              </button>
            ))}
            {!scans.length && <p className="text-sm text-slate-500">Run a scan first.</p>}
          </div>
        </div>
        <div className="grid gap-3">
          {reports.map((report) => (
            <div key={report.id} className="glass rounded p-5">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h2 className="font-semibold">{report.title}</h2>
                  <p className="mt-2 text-sm text-slate-400">{report.summary}</p>
                </div>
                <a href={`${import.meta.env.VITE_API_URL || 'http://localhost:8080/api'}/reports/${report.id}/export`} className="text-neon"><Download size={20} /></a>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
