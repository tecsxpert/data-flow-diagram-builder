import { useEffect, useState, useRef, useMemo } from "react";
import API from "../services/api";

const PAGE_SIZE = 5;

// ── Helpers ──────────────────────────────────────────────────────────────────

const statusBadge = (status) => {
  const map = {
    ACTIVE:    "bg-green-100 text-green-700",
    COMPLETED: "bg-yellow-100 text-yellow-700",
    INACTIVE:  "bg-red-100 text-red-700",
  };
  return map[status] || "bg-gray-100 text-gray-600";
};

// ── Component ─────────────────────────────────────────────────────────────────

function ListPage({ onView, onEdit }) {
  // Raw data from backend (all records)
  const [allData, setAllData] = useState([]);
  const [loading, setLoading]  = useState(true);
  const [error, setError]      = useState("");

  // ── Filter state ──────────────────────────────────────────
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");   // "ACTIVE" | "COMPLETED" | "INACTIVE" | ""
  const [fromDate, setFromDate] = useState("");           // "YYYY-MM-DD"
  const [toDate, setToDate]     = useState("");           // "YYYY-MM-DD"
  const [dateApplied, setDateApplied] = useState(false);  // Only apply date filter when user clicks Apply

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);

  // Debounce ref for search
  const debounceRef = useRef(null);

  // ── Fetch all data on mount ───────────────────────────────
  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await API.get("/all?page=0&size=1000");
      setAllData(res.data.content);
    } catch (err) {
      setError("Failed to load data. Make sure you are logged in.");
    } finally {
      setLoading(false);
    }
  };

  // ── Debounced Search handler ──────────────────────────────
  // Tries the backend search endpoint first (500 ms debounce).
  // Falls back to client-side filtering if the endpoint is unavailable.
  const handleSearch = (value) => {
    setSearchTerm(value);
    setCurrentPage(1);

    clearTimeout(debounceRef.current);

    if (!value.trim()) {
      // No query → just reset to all data (client filter still applies)
      return;
    }

    debounceRef.current = setTimeout(async () => {
      try {
        const res = await API.get(`/search?q=${encodeURIComponent(value)}`);
        // Merge result into allData so client-side filters still work
        // We replace allData with server search result
        setAllData(res.data);
      } catch (err) {
        // Backend search not available — client-side filter handles it
      }
    }, 500);
  };

  // ── Status filter handler ─────────────────────────────────
  const handleStatusFilter = async (status) => {
    setStatusFilter(status);
    setCurrentPage(1);

    if (!status) {
      // Clear status filter → reload all
      fetchAll();
      setSearchTerm("");
      return;
    }

    // Try backend status filter endpoint
    try {
      const res = await API.get(`/search?status=${status}`);
      setAllData(res.data);
      setSearchTerm("");
    } catch (err) {
      // Backend doesn't support ?status= → client-side filter handles it silently
    }
  };

  // ── Date filter handler ───────────────────────────────────
  const handleDateFilter = async () => {
    if (!fromDate || !toDate) {
      alert("Please select both From and To dates.");
      return;
    }
    if (fromDate > toDate) {
      alert("'From' date cannot be after 'To' date.");
      return;
    }

    setDateApplied(true);
    setCurrentPage(1);

    // Try backend date filter endpoint
    try {
      const res = await API.get(`/filter?from=${fromDate}&to=${toDate}`);
      setAllData(res.data);
      setSearchTerm("");
      setStatusFilter("");
    } catch (err) {
      // Backend /filter not ready → client-side filter will handle it
    }
  };

  // ── Clear all filters ─────────────────────────────────────
  const clearAllFilters = () => {
    setSearchTerm("");
    setStatusFilter("");
    setFromDate("");
    setToDate("");
    setDateApplied(false);
    setCurrentPage(1);
    fetchAll();
  };

  // ── Delete handler ────────────────────────────────────────
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this record?")) return;
    try {
      await API.delete(`/${id}`);
      fetchAll();
      setSearchTerm("");
      setStatusFilter("");
      setFromDate("");
      setToDate("");
      setDateApplied(false);
    } catch (err) {
    }
  };

  // ── Client-side filtering (runs on every render) ──────────
  const filteredData = useMemo(() => {
    let result = [...allData];

    // 1. Search filter (client-side text match)
    if (searchTerm.trim()) {
      const q = searchTerm.toLowerCase();
      result = result.filter(
        (item) =>
          item.title?.toLowerCase().includes(q) ||
          item.description?.toLowerCase().includes(q)
      );
    }

    // 2. Status filter
    if (statusFilter) {
      result = result.filter((item) => item.status === statusFilter);
    }

    // 3. Date range filter (client-side, only if dateApplied)
    if (dateApplied && fromDate && toDate) {
      result = result.filter((item) => {
        if (!item.createdAt) return false;
        const created = item.createdAt.substring(0, 10);
        return created >= fromDate && created <= toDate;
      });
    }

    return result;
  }, [allData, searchTerm, statusFilter, dateApplied, fromDate, toDate]);

  // ── Pagination on filtered data ───────────────────────────
  const totalPages   = Math.ceil(filteredData.length / PAGE_SIZE);
  const paginatedData = filteredData.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE
  );

  const goToPage = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // ── Active filter count (for badge) ──────────────────────
  const activeFilterCount = [
    searchTerm.trim() !== "",
    statusFilter !== "",
    dateApplied,
  ].filter(Boolean).length;

  // ── Loading / Error states ────────────────────────────────
  if (loading) return <p className="text-center mt-8 text-gray-400">Loading records...</p>;
  if (error)   return <p className="text-red-500 text-center mt-8">{error}</p>;

  // ── Render ────────────────────────────────────────────────
  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-5">

      {/* ── Header ────────────────────────────────────────── */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-bold text-gray-800">📋 DFD Records</h2>
          {activeFilterCount > 0 && (
            <span className="bg-blue-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
              {activeFilterCount} filter{activeFilterCount > 1 ? "s" : ""} active
            </span>
          )}
        </div>
        {activeFilterCount > 0 && (
          <button
            id="clear-filters-btn"
            onClick={clearAllFilters}
            className="text-sm text-red-500 hover:underline"
          >
            ✕ Clear all filters
          </button>
        )}
      </div>

      {/* ── Filter Bar ────────────────────────────────────── */}
      <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-5 space-y-3">

        {/* Row 1: Search + Status */}
        <div className="flex flex-wrap gap-3 items-end">
          {/* 🔍 Debounced Search */}
          <div className="flex-1 min-w-[180px]">
            <label className="block text-xs font-semibold text-gray-500 mb-1">
              🔍 Search
            </label>
            <input
              id="search-input"
              type="text"
              placeholder="Search by title or description..."
              value={searchTerm}
              className="border border-gray-300 rounded w-full p-2 h-11 text-sm md:text-base focus:outline-none focus:ring-2 focus:ring-blue-400"
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>

          {/* 🏷 Status Filter */}
          <div className="min-w-[160px]">
            <label className="block text-xs font-semibold text-gray-500 mb-1">
              🏷 Status
            </label>
            <select
              id="status-filter"
              value={statusFilter}
              className="border border-gray-300 rounded w-full p-2 h-11 text-sm md:text-base focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white"
              onChange={(e) => handleStatusFilter(e.target.value)}
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">✅ Active</option>
              <option value="COMPLETED">🏁 Completed</option>
              <option value="INACTIVE">❌ Inactive</option>
            </select>
          </div>
        </div>

        {/* Row 2: Date Range */}
        <div className="flex flex-wrap gap-3 items-end">
          {/* From Date */}
          <div>
            <label className="block text-xs font-semibold text-gray-500 mb-1">
              📅 From Date
            </label>
            <input
              id="from-date"
              type="date"
              value={fromDate}
              max={toDate || undefined}
              className="border border-gray-300 rounded p-2 h-11 text-sm md:text-base focus:outline-none focus:ring-2 focus:ring-blue-400"
              onChange={(e) => {
                setFromDate(e.target.value);
                setDateApplied(false); // Reset until Apply clicked
              }}
            />
          </div>

          {/* To Date */}
          <div>
            <label className="block text-xs font-semibold text-gray-500 mb-1">
              📅 To Date
            </label>
            <input
              id="to-date"
              type="date"
              value={toDate}
              min={fromDate || undefined}
              className="border border-gray-300 rounded p-2 h-11 text-sm md:text-base focus:outline-none focus:ring-2 focus:ring-blue-400"
              onChange={(e) => {
                setToDate(e.target.value);
                setDateApplied(false); // Reset until Apply clicked
              }}
            />
          </div>

          {/* Apply Button */}
          <button
            id="apply-date-filter-btn"
            onClick={handleDateFilter}
            disabled={!fromDate || !toDate}
            className="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-300 text-white px-4 py-2 rounded text-sm font-semibold transition"
          >
            Apply
          </button>

          {/* Clear Date */}
          {dateApplied && (
            <button
              id="clear-date-btn"
              onClick={() => {
                setFromDate("");
                setToDate("");
                setDateApplied(false);
                setCurrentPage(1);
              }}
              className="text-xs text-gray-500 hover:text-red-500 underline self-center"
            >
              Clear dates
            </button>
          )}
        </div>
      </div>

      {/* ── Active Filter Pills ────────────────────────────── */}
      {activeFilterCount > 0 && (
        <div className="flex flex-wrap gap-2 mb-4">
          {searchTerm.trim() && (
            <span className="flex items-center gap-1 bg-blue-50 text-blue-700 border border-blue-200 text-xs px-3 py-1 rounded-full">
              Search: "{searchTerm}"
              <button onClick={() => { setSearchTerm(""); fetchAll(); }} className="ml-1 hover:text-red-500">✕</button>
            </span>
          )}
          {statusFilter && (
            <span className="flex items-center gap-1 bg-green-50 text-green-700 border border-green-200 text-xs px-3 py-1 rounded-full">
              Status: {statusFilter}
              <button onClick={() => handleStatusFilter("")} className="ml-1 hover:text-red-500">✕</button>
            </span>
          )}
          {dateApplied && (
            <span className="flex items-center gap-1 bg-purple-50 text-purple-700 border border-purple-200 text-xs px-3 py-1 rounded-full">
              Date: {fromDate} → {toDate}
              <button onClick={() => { setFromDate(""); setToDate(""); setDateApplied(false); }} className="ml-1 hover:text-red-500">✕</button>
            </span>
          )}
        </div>
      )}

      {/* ── Results count ─────────────────────────────────── */}
      <p className="text-xs text-gray-400 mb-3">
        Showing {filteredData.length} of {allData.length} record{allData.length !== 1 ? "s" : ""}
      </p>

      {/* ── Table ─────────────────────────────────────────── */}
      {filteredData.length === 0 ? (
        <div className="text-center py-12 text-gray-400">
          <p className="text-3xl mb-2">🔍</p>
          <p>No records match your filters.</p>
          <button onClick={clearAllFilters} className="mt-2 text-blue-500 hover:underline text-sm">
            Clear filters
          </button>
        </div>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="table-auto w-full text-sm">
              <thead>
                <tr className="bg-gray-50 text-gray-600 text-left">
                  <th className="p-2 border-b font-semibold">ID</th>
                  <th className="p-2 border-b font-semibold">Title</th>
                  <th className="p-2 border-b font-semibold">Status</th>
                  <th className="p-2 border-b font-semibold">Created</th>
                  <th className="p-2 border-b font-semibold">Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginatedData.map((item) => (
                  <tr key={item.id} className="hover:bg-gray-50 border-b last:border-0">
                    <td className="p-2 text-gray-400 text-xs">{item.id}</td>
                    <td className="p-2 font-medium text-gray-800">{item.title}</td>
                    <td className="p-2">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${statusBadge(item.status)}`}>
                        {item.status || "—"}
                      </span>
                    </td>
                    <td className="p-2 text-gray-500">
                      {item.createdAt ? item.createdAt.substring(0, 10) : "—"}
                    </td>
                    <td className="p-2">
                      <div className="flex gap-2">
                        <button
                          id={`view-btn-${item.id}`}
                          onClick={() => onView && onView(item.id)}
                          className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded text-sm md:text-base font-medium transition h-11"
                        >
                          👁 View
                        </button>
                        <button
                          id={`edit-btn-${item.id}`}
                          onClick={() => onEdit && onEdit(item.id)}
                          className="bg-blue-100 hover:bg-blue-200 text-blue-700 px-4 py-2 rounded text-sm md:text-base font-medium transition h-11"
                        >
                          ✏️ Edit
                        </button>
                        <button
                          id={`delete-btn-${item.id}`}
                          onClick={() => handleDelete(item.id)}
                          className="bg-red-100 hover:bg-red-200 text-red-700 px-4 py-2 rounded text-sm md:text-base font-medium transition h-11"
                        >
                          🗑 Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* ── Pagination ──────────────────────────────── */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between mt-4 text-sm">
              <p className="text-gray-500">
                Page {currentPage} of {totalPages}
              </p>
              <div className="flex gap-2">
                <button
                  id="prev-page-btn"
                  onClick={() => goToPage(currentPage - 1)}
                  disabled={currentPage === 1}
                  className="px-3 py-1 border rounded disabled:opacity-40 hover:bg-gray-100 transition"
                >
                  ← Prev
                </button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                  <button
                    key={page}
                    id={`page-btn-${page}`}
                    onClick={() => goToPage(page)}
                    className={`px-3 py-1 border rounded transition ${
                      page === currentPage
                        ? "bg-blue-500 text-white border-blue-500"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    {page}
                  </button>
                ))}
                <button
                  id="next-page-btn"
                  onClick={() => goToPage(currentPage + 1)}
                  disabled={currentPage === totalPages}
                  className="px-3 py-1 border rounded disabled:opacity-40 hover:bg-gray-100 transition"
                >
                  Next →
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default ListPage;
