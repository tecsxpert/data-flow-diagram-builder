import { useState } from "react";
import authApi from "../services/authApi";

function RegisterPage({ onGoToLogin }) {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRegister = async () => {
    setError("");
    setSuccess("");

    // Validation
    if (!form.name || !form.email || !form.password || !form.confirmPassword) {
      setError("All fields are required");
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (form.password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    setLoading(true);

    try {
      await authApi.post("/register", {
        name: form.name,
        email: form.email,
        password: form.password,
      });

      setSuccess("Account created! You can now log in.");
      setForm({ name: "", email: "", password: "", confirmPassword: "" });

      // Auto-redirect to login after 2 seconds
      setTimeout(() => {
        onGoToLogin();
      }, 2000);
    } catch (err) {
      const msg = err.response?.data?.message || "Registration failed. Email may already be in use.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleRegister();
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-sm">
        <h1 className="text-2xl font-bold text-center text-green-600 mb-6">
          📝 Register
        </h1>

        {error && (
          <p className="bg-red-100 text-red-600 text-sm px-3 py-2 rounded mb-4">
            {error}
          </p>
        )}

        {success && (
          <p className="bg-green-100 text-green-600 text-sm px-3 py-2 rounded mb-4">
            {success}
          </p>
        )}

        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Name
          </label>
          <input
            id="register-name"
            name="name"
            type="text"
            placeholder="John Doe"
            value={form.name}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-green-400"
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
        </div>

        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            id="register-email"
            name="email"
            type="email"
            placeholder="you@example.com"
            value={form.email}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-green-400"
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
        </div>

        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            id="register-password"
            name="password"
            type="password"
            placeholder="Min 6 characters"
            value={form.password}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-green-400"
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
        </div>

        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Confirm Password
          </label>
          <input
            id="register-confirm-password"
            name="confirmPassword"
            type="password"
            placeholder="Repeat password"
            value={form.confirmPassword}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-green-400"
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
        </div>

        <button
          id="register-btn"
          onClick={handleRegister}
          disabled={loading}
          className="bg-green-500 hover:bg-green-600 disabled:bg-green-300 text-white w-full py-2 rounded font-semibold transition"
        >
          {loading ? "Registering..." : "Register"}
        </button>

        <p className="text-center text-sm text-gray-500 mt-4">
          Already have an account?{" "}
          <button
            id="go-to-login"
            onClick={onGoToLogin}
            className="text-green-500 hover:underline font-medium"
          >
            Login
          </button>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
