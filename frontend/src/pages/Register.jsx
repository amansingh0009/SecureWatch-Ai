import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../utils/errorMessage';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const submit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await register(event.currentTarget.name.value, event.currentTarget.email.value, event.currentTarget.password.value);
      navigate('/dashboard');
    } catch (err) {
      setError(errorMessage(err, 'Registration failed'));
    }
  };
  return (
    <main className="grid min-h-screen place-items-center px-4">
      <form onSubmit={submit} className="glass w-full max-w-md rounded p-6">
        <h1 className="text-2xl font-semibold">Create SecureWatch account</h1>
        <input name="name" required placeholder="Name" className="mt-6 w-full rounded border border-line bg-ink px-4 py-3 outline-none focus:border-neon" />
        <input name="email" type="email" required placeholder="Email" className="mt-3 w-full rounded border border-line bg-ink px-4 py-3 outline-none focus:border-neon" />
        <input name="password" type="password" minLength="8" required placeholder="Password" className="mt-3 w-full rounded border border-line bg-ink px-4 py-3 outline-none focus:border-neon" />
        {error && <p className="mt-3 text-sm text-danger">{error}</p>}
        <button className="mt-5 w-full rounded bg-neon px-4 py-3 font-semibold text-ink">Register</button>
        <div className="mt-4 text-center text-sm text-slate-400"><Link to="/login" className="text-neon">Already have an account?</Link></div>
      </form>
    </main>
  );
}
