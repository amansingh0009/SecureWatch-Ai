import { useEffect, useState } from 'react';
import { Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import api from '../api/client';
import StatCard from '../components/StatCard';

const colors = { CRITICAL: '#ff5876', HIGH: '#ff8a65', MEDIUM: '#f8c14a', LOW: '#48d7ff' };

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  useEffect(() => { api.get('/dashboard').then(({ data }) => setStats(data)); }, []);
  const severityData = Object.entries(stats?.vulnerabilitiesBySeverity || {}).map(([name, value]) => ({ name, value }));
  const scanData = Object.entries(stats?.scansByType || {}).map(([name, value]) => ({ name, value }));
  return (
    <div>
      <h1 className="text-3xl font-semibold">Security Dashboard</h1>
      <p className="mt-2 text-slate-400">Live scan posture, threat counters, and activity signals.</p>
      <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Total scans" value={stats?.totalScans ?? '...'} detail="Website, port, and malware workflows" />
        <StatCard title="Critical threats" value={stats?.criticalThreats ?? '...'} tone="danger" detail="Needs immediate attention" />
        <StatCard title="High threats" value={stats?.highThreats ?? '...'} tone="amber" detail="Prioritize this sprint" />
        <StatCard title="Avg risk score" value={Math.round(stats?.averageRiskScore || 0)} tone="cyan" detail="0 is healthy, 100 is severe" />
      </div>
      <div className="mt-6 grid gap-4 xl:grid-cols-2">
        <div className="glass rounded p-5">
          <h2 className="mb-4 font-semibold">Vulnerabilities by severity</h2>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie data={severityData} dataKey="value" nameKey="name" outerRadius={100}>
                {severityData.map((entry) => <Cell key={entry.name} fill={colors[entry.name]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="glass rounded p-5">
          <h2 className="mb-4 font-semibold">Scans by type</h2>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={scanData}>
              <CartesianGrid stroke="#20353a" />
              <XAxis dataKey="name" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip />
              <Bar dataKey="value" fill="#38f2af" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
