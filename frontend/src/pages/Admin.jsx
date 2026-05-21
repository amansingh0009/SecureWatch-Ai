import { useEffect, useState } from 'react';
import api from '../api/client';

export default function Admin() {
  const [users, setUsers] = useState([]);
  const [logs, setLogs] = useState([]);
  useEffect(() => {
    Promise.all([api.get('/admin/users'), api.get('/admin/logs')]).then(([u, l]) => {
      setUsers(u.data);
      setLogs(l.data);
    });
  }, []);
  return (
    <div>
      <h1 className="text-3xl font-semibold">Admin Panel</h1>
      <p className="mt-2 text-slate-400">Manage users, audit activity, and monitor suspicious events.</p>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <section className="glass rounded p-5">
          <h2 className="mb-4 font-semibold">Users</h2>
          <div className="grid gap-3">
            {users.map((user) => (
              <div key={user.id} className="rounded border border-line bg-ink p-3">
                <p className="font-medium">{user.name}</p>
                <p className="text-sm text-slate-400">{user.email}</p>
                <p className="mt-1 text-xs text-neon">{user.roles?.join(', ')}</p>
              </div>
            ))}
          </div>
        </section>
        <section className="glass rounded p-5">
          <h2 className="mb-4 font-semibold">Activity Logs</h2>
          <div className="grid gap-3">
            {logs.map((log) => (
              <div key={log.id} className={`rounded border p-3 ${log.suspicious ? 'border-danger/50 bg-danger/10' : 'border-line bg-ink'}`}>
                <p className="font-medium">{log.action}</p>
                <p className="text-sm text-slate-400">{log.details}</p>
                <p className="mt-1 text-xs text-slate-500">{log.userEmail || 'anonymous'} | {log.ipAddress}</p>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
