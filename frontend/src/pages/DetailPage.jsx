import { useEffect, useState } from "react";
import API from "../services/api";
import AIPanel from "../components/AIPanel";

function DetailPage({ id, onBack, onEdit }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await API.get(`/${id}`);
      setData(res.data);
    } catch (err) {
      setError("Failed to load record details.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="p-6 text-center text-gray-500">
        Loading record...
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6">
        <p className="text-red-500 mb-3">{error}</p>
        <button
          onClick={onBack}
          className="text-blue-500 hover:underline text-sm"
        >
          ← Back to Records
        </button>
      </div>
    );
  }

  const statusColor =
    data.status === "ACTIVE"
      ? "bg-green-100 text-green-700"
      : data.status === "COMPLETED"
      ? "bg-yellow-100 text-yellow-700"
      : data.status === "INACTIVE"
      ? "bg-red-100 text-red-700"
      : "bg-gray-100 text-gray-600";

  return (
    <div className="p-6 max-w-2xl mx-auto">
      {/* Back button */}
      <button
        id="back-btn"
        onClick={onBack}
        className="text-blue-500 hover:underline text-sm mb-5 flex items-center gap-1"
      >
        ← Back to Records
      </button>

      {/* Detail Card */}
      <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-6">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-2xl font-bold text-gray-800">{data.title}</h1>
          <span className={`px-3 py-1 rounded-full text-sm font-semibold ${statusColor}`}>
            {data.status || "—"}
          </span>
        </div>

        <hr className="mb-4" />

        <div className="space-y-3 text-sm text-gray-700">
          <div className="flex gap-2">
            <span className="font-semibold w-28 text-gray-500">ID:</span>
            <span>{data.id}</span>
          </div>
          <div className="flex gap-2">
            <span className="font-semibold w-28 text-gray-500">Description:</span>
            <span>{data.description || "—"}</span>
          </div>
          <div className="flex gap-2">
            <span className="font-semibold w-28 text-gray-500">Created:</span>
            <span>{data.createdAt ? data.createdAt.substring(0, 10) : "—"}</span>
          </div>
          <div className="flex gap-2">
            <span className="font-semibold w-28 text-gray-500">Updated:</span>
            <span>{data.updatedAt ? data.updatedAt.substring(0, 10) : "—"}</span>
          </div>
        </div>

        {/* Actions */}
        <div className="flex gap-3 mt-6">
          <button
            id="detail-edit-btn"
            onClick={() => onEdit(data.id)}
            className="bg-blue-500 hover:bg-blue-600 text-white px-5 py-2 rounded font-medium text-sm transition"
          >
            ✏️ Edit
          </button>
        </div>
      </div>

      {/* ── AI Panel ────────────────────────────────────── */}
      <AIPanel description={data.description} />
    </div>
  );
}

export default DetailPage;
