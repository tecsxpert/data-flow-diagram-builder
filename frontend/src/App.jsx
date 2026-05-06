import { useContext, useState } from "react";
import { AuthContext } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import Dashboard from "./pages/Dashboard";
import ListPage from "./pages/ListPage";
import FormPage from "./pages/FormPage";
import DetailPage from "./pages/DetailPage";
import FileUpload from "./components/FileUpload";
import Analytics from "./pages/Analytics";
import ProtectedRoute from "./components/ProtectedRoute";

// View names: 'dashboard' | 'records' | 'detail' | 'create' | 'edit' | 'upload'
function App() {
  const { token, setToken } = useContext(AuthContext);

  // Auth screen toggle
  const [showRegister, setShowRegister] = useState(false);

  // Navigation state (replaces React Router for simplicity)
  const [currentView, setCurrentView] = useState("dashboard");
  const [selectedId, setSelectedId] = useState(null);

  // ── Auth Handlers ───────────────────────────────────────
  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setCurrentView("dashboard");
  };

  // ── Navigation Handlers ─────────────────────────────────
  const goTo = (view, id = null) => {
    setCurrentView(view);
    setSelectedId(id);
  };

  // ── Not Logged In ────────────────────────────────────────
  if (!token) {
    if (showRegister) {
      return <RegisterPage onGoToLogin={() => setShowRegister(false)} />;
    }
    return (
      <LoginPage
        setToken={setToken}
        onGoToRegister={() => setShowRegister(true)}
      />
    );
  }

  // ── Nav tabs config ──────────────────────────────────────
  const NAV_TABS = [
    { id: "dashboard", label: "📊 Dashboard" },
    { id: "records",   label: "📋 Records"   },
    { id: "analytics", label: "📈 Analytics" },
    { id: "create",    label: "➕ Create"    },
    { id: "upload",    label: "📤 Upload"    },
  ];

  // ── Render Active View ───────────────────────────────────
  const renderView = () => {
    switch (currentView) {
      case "dashboard":
        return <Dashboard />;

      case "records":
        return (
          <ListPage
            onView={(id) => goTo("detail", id)}
            onEdit={(id) => goTo("edit", id)}
          />
        );

      case "create":
        return (
          <FormPage
            onSaved={() => goTo("records")}
          />
        );

      case "edit":
        return (
          <div>
            <button
              onClick={() => goTo("records")}
              className="text-blue-500 hover:underline text-sm mb-4 flex items-center gap-1"
            >
              ← Back to Records
            </button>
            <FormPage
              editId={selectedId}
              onSaved={() => goTo("records")}
            />
          </div>
        );

      case "detail":
        return (
          <DetailPage
            id={selectedId}
            onBack={() => goTo("records")}
            onEdit={(id) => goTo("edit", id)}
          />
        );

      case "analytics":
        return <Analytics />;

      case "upload":
        return <FileUpload />;

      default:
        return <Dashboard />;
    }
  };

  // ── Logged In Layout ─────────────────────────────────────
  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-gray-50">
        {/* Top Navigation Bar */}
        <header className="bg-white shadow-sm border-b border-gray-200">
          <div className="container mx-auto max-w-5xl px-4 py-3 flex items-center justify-between">
            {/* Logo */}
            <span className="text-lg font-bold text-blue-600 select-none">
              📊 DFD Builder
            </span>

            {/* Nav Tabs */}
            <nav className="flex gap-1">
              {NAV_TABS.map((tab) => (
                <button
                  key={tab.id}
                  id={`nav-${tab.id}`}
                  onClick={() => goTo(tab.id)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                    currentView === tab.id
                      ? "bg-blue-500 text-white"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </nav>

            {/* Logout */}
            <button
              id="logout-btn"
              onClick={handleLogout}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-1.5 rounded text-sm font-medium transition"
            >
              Logout
            </button>
          </div>
        </header>

        {/* Main Content */}
        <main className="container mx-auto max-w-5xl px-4 py-6">
          {renderView()}
        </main>
      </div>
    </ProtectedRoute>
  );
}

export default App;
