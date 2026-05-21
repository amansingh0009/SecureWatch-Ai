import { useState } from 'react';
import api from '../api/client';
import ScannerForm from '../components/ScannerForm';
import VulnerabilityTable from '../components/VulnerabilityTable';

export default function PortScanner() {
  const [scan, setScan] = useState(null);
  const [loading, setLoading] = useState(false);
  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      const { data } = await api.post('/scans/ports', { target: event.currentTarget.target.value });
      setScan(data);
    } finally {
      setLoading(false);
    }
  };
  return (
    <div>
      <h1 className="text-3xl font-semibold">Port Scanner</h1>
      <p className="mt-2 text-slate-400">Runs Nmap service detection against permitted assets.</p>
      <div className="mt-6"><ScannerForm title="Domain or IP" placeholder="scanme.nmap.org" button="Scan ports" loading={loading} onSubmit={submit} /></div>
      {scan && (
        <div className="grid gap-5">
          <div className="glass rounded p-5">
            <p className="text-sm text-slate-400">Open ports</p>
            <p className="text-4xl font-semibold text-cyan">{scan.openPorts}</p>
            <p className="mt-4 text-slate-300">{scan.aiSummary}</p>
          </div>
          <VulnerabilityTable items={scan.vulnerabilities} />
        </div>
      )}
    </div>
  );
}
