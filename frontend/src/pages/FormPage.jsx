import { useState, useEffect } from "react";
import API from "../services/api";

// editId = null → Create mode
// editId = number → Edit mode (pre-fills form from API)
function FormPage({ editId = null, onSaved }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    status: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const isEditMode = editId !== null;

  // If editing, pre-load the record
  useEffect(() => {
    if (isEditMode) {
      API.get(`/${editId}`)
        .then((res) => {
          const { title, description, status } = res.data;
          setForm({ title: title || "", description: description || "", status: status || "" });
        })
        .catch((err) => {
          setError("Failed to load record for editing");
        });
    }
  }, [editId]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
    setSuccess("");
  };

  const handleSubmit = async () => {
    if (!form.title.trim()) {
      setError("Title is required");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      if (isEditMode) {
        await API.put(`/${editId}`, form);
        setSuccess("Record updated successfully!");
      } else {
        await API.post("/create", form);
        setSuccess("Record created successfully!");
        setForm({ title: "", description: "", status: "" });
      }

      // Notify parent so it can refresh list
      if (onSaved) onSaved();
    } catch (err) {
      setError(isEditMode ? "Failed to update record" : "Failed to create record");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-6">
      <h2 className="text-xl font-bold text-gray-800 mb-4">
        {isEditMode ? "✏️ Edit Record" : "➕ Create Record"}
      </h2>

      {error && (
        <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded mb-4">
          {error}
        </p>
      )}
      {success && (
        <p className="bg-green-50 text-green-600 text-sm px-3 py-2 rounded mb-4">
          {success}
        </p>
      )}

      <div className="space-y-3">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Title *</label>
          <input
            id="form-title"
            name="title"
            type="text"
            placeholder="Enter title"
            value={form.title}
            className="border border-gray-300 rounded w-full p-2 h-11 focus:outline-none focus:ring-2 focus:ring-blue-400"
            onChange={handleChange}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <textarea
            id="form-description"
            name="description"
            placeholder="Enter description"
            value={form.description}
            rows={3}
            className="border border-gray-300 rounded w-full p-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
            onChange={handleChange}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
          <select
            id="form-status"
            name="status"
            value={form.status}
            className="border border-gray-300 rounded w-full p-2 h-11 focus:outline-none focus:ring-2 focus:ring-blue-400"
            onChange={handleChange}
          >
            <option value="">— Select Status —</option>
            <option value="ACTIVE">Active</option>
            <option value="COMPLETED">Completed</option>
            <option value="INACTIVE">Inactive</option>
          </select>
        </div>
      </div>

      <div className="flex gap-3 mt-5">
        <button
          id="form-submit-btn"
          onClick={handleSubmit}
          disabled={loading}
          className="bg-blue-500 hover:bg-blue-600 disabled:bg-blue-300 text-white px-6 py-2 rounded font-semibold text-sm transition"
        >
          {loading ? "Saving..." : isEditMode ? "Update" : "Create"}
        </button>
      </div>
    </div>
  );
}

export default FormPage;
