import { useEffect, useState } from 'react';
import api from '../api/client';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  useEffect(() => { api.get('/profile').then(({ data }) => setProfile(data)); }, []);
  return (
    <div>
      <h1 className="text-3xl font-semibold">Profile</h1>
      <div className="glass mt-6 max-w-xl rounded p-5">
        <p className="text-sm text-slate-400">Name</p>
        <p className="text-xl font-semibold">{profile?.name}</p>
        <p className="mt-4 text-sm text-slate-400">Email</p>
        <p>{profile?.email}</p>
        <p className="mt-4 text-sm text-slate-400">Roles</p>
        <p className="text-neon">{profile?.roles?.join(', ')}</p>
      </div>
    </div>
  );
}
