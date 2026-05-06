import { useState } from "react";
import authApi from "../services/authApi";

// ── Demo credentials (frontend-only bypass) ───────────────────────────────────
// When the backend JWT auth is not ready, these credentials allow full UI testing.
const DEMO_EMAIL    = "demo@dfd.com";
const DEMO_PASSWORD = "demo123";
const DEMO_TOKEN    = "demo-mock-token-dfd-2024"; // fake token for frontend testing

function LoginPage({ setToken, onGoToRegister }) {
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState("");
  const [loading, setLoading]   = useState(false);

  // Auto-fill demo credentials
  const fillDemo = () => {
    setEmail(DEMO_EMAIL);
    setPassword(DEMO_PASSWORD);
    setError("");
  };

  const handleLogin = async () => {
    if (!email || !password) {
      setError("Please fill in all fields");
      return;
    }

    setLoading(true);
    setError("");

    // ── Demo bypass (no backend needed) ──────────────────────
    if (email === DEMO_EMAIL && password === DEMO_PASSWORD) {
      setTimeout(() => {
        localStorage.setItem("token", DEMO_TOKEN);
        setToken(DEMO_TOKEN);
        setLoading(false);
      }, 600); // small delay to simulate a real login
      return;
    }

    // ── Real backend login ────────────────────────────────────
    try {
      const res = await authApi.post("/login", { email, password });
      const token = res.data.token;
      localStorage.setItem("token", token);
      setToken(token);
    } catch (err) {
      setError("Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleLogin();
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-sm">

        {/* Title */}
        <h1 className="text-2xl font-bold text-center text-blue-600 mb-2">
          🔐 Login
        </h1>
        <p className="text-center text-sm text-gray-400 mb-6">
          DFD Builder — Secure Access
        </p>

        {/* ── Demo Credentials Banner ─────────────────────── */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-5">
          <p className="text-xs font-semibold text-blue-700 mb-1">
            🧪 Demo Credentials (for testing)
          </p>
          <div className="flex items-center justify-between">
            <div className="text-xs text-blue-600 space-y-0.5">
              <p>📧 <span className="font-mono font-semibold">{DEMO_EMAIL}</span></p>
              <p>🔑 <span className="font-mono font-semibold">{DEMO_PASSWORD}</span></p>
            </div>
            <button
              id="use-demo-btn"
              onClick={fillDemo}
              className="bg-blue-500 hover:bg-blue-600 text-white text-xs px-3 py-1.5 rounded font-semibold transition"
            >
              Use Demo
            </button>
          </div>
        </div>

        {/* Error */}
        {error && (
          <p className="bg-red-100 text-red-600 text-sm px-3 py-2 rounded mb-4">
            {error}
          </p>
        )}

        {/* Email */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            id="login-email"
            type="email"
            placeholder="you@example.com"
            value={email}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={handleKeyDown}
          />
        </div>

        {/* Password */}
        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            id="login-password"
            type="password"
            placeholder="••••••••"
            value={password}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={handleKeyDown}
          />
        </div>

        {/* Login Button */}
        <button
          id="login-btn"
          onClick={handleLogin}
          disabled={loading}
          className="bg-blue-500 hover:bg-blue-600 disabled:bg-blue-300 text-white w-full py-2 rounded font-semibold transition"
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        {/* Register link */}
        <p className="text-center text-sm text-gray-500 mt-4">
          Don't have an account?{" "}
          <button
            id="go-to-register"
            onClick={onGoToRegister}
            className="text-blue-500 hover:underline font-medium"
          >
            Register
          </button>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
