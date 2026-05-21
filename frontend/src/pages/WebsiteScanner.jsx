import { useState } from 'react';
import api from '../api/client';
import ScannerForm from '../components/ScannerForm';
import VulnerabilityTable from '../components/VulnerabilityTable';

export default function WebsiteScanner() {
  const [scan, setScan] = useState(null);
  const [loading, setLoading] = useState(false);
  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      const { data } = await api.post('/scans/website', { target: event.currentTarget.target.value });
      setScan(data);
    } finally {
      setLoading(false);
    }
  };
  return (
    <div>
      <h1 className="text-3xl font-semibold">Website Vulnerability Scanner</h1>
      <p className="mt-2 text-slate-400">Checks security headers, TLS usage, risky methods, and OWASP-style passive indicators.</p>
      <div className="mt-6"><ScannerForm title="Target URL" placeholder="https://example.com" button="Scan website" loading={loading} onSubmit={submit} /></div>
      {scan && <Result scan={scan} />}
    </div>
  );
}

function Result({ scan }) {
  return (
    <div className="grid gap-5">
      <div className="glass rounded p-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div><p className="text-sm text-slate-400">Risk score</p><p className="text-4xl font-semibold text-neon">{scan.riskScore}</p></div>
          <span className="rounded bg-white/5 px-3 py-1 text-sm">{scan.status}</span>
        </div>
        <p className="mt-4 text-slate-300">{scan.aiSummary}</p>
      </div>
      <VulnerabilityTable items={scan.vulnerabilities} />
    </div>
  );
}
