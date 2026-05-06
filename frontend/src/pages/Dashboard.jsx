import { useEffect, useState } from "react";
import API from "../services/api";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  Cell,
} from "recharts";

const COLORS = ["#3b82f6", "#22c55e", "#f59e0b", "#ef4444"];

const KPI_CONFIG = [
  { key: "total",     label: "Total Records", bg: "bg-blue-50",   border: "border-blue-400",  text: "text-blue-600",  icon: "📋" },
  { key: "active",    label: "Active",         bg: "bg-green-50",  border: "border-green-400", text: "text-green-600", icon: "✅" },
  { key: "completed", label: "Completed",      bg: "bg-yellow-50", border: "border-yellow-400",text: "text-yellow-600",icon: "🏁" },
  { key: "deleted",   label: "Deleted",        bg: "bg-red-50",    border: "border-red-400",   text: "text-red-600",   icon: "🗑️" },
];

function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await API.get("/stats");
      setStats(res.data);
    } catch (err) {
      // Graceful fallback — backend /stats may not be ready yet
      setError("Stats API not available. Showing placeholder data.");
      setStats({ total: 0, active: 0, completed: 0, deleted: 0 });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin text-4xl mb-3">📊</div>
          <p className="text-gray-500">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  const chartData = [
    { name: "Total",     value: stats.total },
    { name: "Active",    value: stats.active },
    { name: "Completed", value: stats.completed },
    { name: "Deleted",   value: stats.deleted },
  ];

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-800">📊 Dashboard</h1>
        <p className="text-gray-500 text-sm mt-1">Overview of your DFD records</p>
      </div>

      {error && (
        <div className="bg-yellow-50 border border-yellow-300 text-yellow-700 text-sm px-4 py-2 rounded mb-6">
          ⚠️ {error}
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {KPI_CONFIG.map((kpi) => (
          <div
            key={kpi.key}
            className={`${kpi.bg} border-l-4 ${kpi.border} rounded-lg p-4 shadow-sm`}
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-2xl">{kpi.icon}</span>
              <span className={`text-3xl font-bold ${kpi.text}`}>
                {stats[kpi.key] ?? 0}
              </span>
            </div>
            <p className="text-sm text-gray-600 font-medium">{kpi.label}</p>
          </div>
        ))}
      </div>

      {/* Bar Chart */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-5">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">
          📈 Records Overview
        </h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="name" tick={{ fontSize: 13 }} />
            <YAxis allowDecimals={false} tick={{ fontSize: 13 }} />
            <Tooltip
              contentStyle={{ borderRadius: "8px", fontSize: "13px" }}
              cursor={{ fill: "rgba(0,0,0,0.04)" }}
            />
            <Bar dataKey="value" radius={[4, 4, 0, 0]}>
              {chartData.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Refresh button */}
      <div className="mt-4 text-right">
        <button
          id="refresh-stats-btn"
          onClick={fetchStats}
          className="text-sm text-blue-500 hover:underline"
        >
          🔄 Refresh Stats
        </button>
      </div>
    </div>
  );
}

export default Dashboard;
