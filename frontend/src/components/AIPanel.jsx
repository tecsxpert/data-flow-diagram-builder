import { useState } from "react";
import API from "../services/api";

/**
 * AIPanel — Day 8 AI Integration UI
 *
 * Props:
 *   description  {string}  — text fed to the AI endpoints
 */
function AIPanel({ description }) {
  const [loading, setLoading] = useState(false);
  const [activeAction, setActiveAction] = useState(null); // which button is running
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  // ─── Call AI endpoint ────────────────────────────────────────────────────────
  const callAI = async (endpoint, label) => {
    if (!description) {
      setError("No description available to analyse.");
      return;
    }
    setLoading(true);
    setActiveAction(label);
    setResult(null);
    setError(null);

    try {
      const res = await API.post(`/ai/${endpoint}`, { text: description });
      setResult({ action: label, data: res.data });
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          "AI service is unavailable. Please try again later."
      );
    } finally {
      setLoading(false);
      setActiveAction(null);
    }
  };

  // ─── Render formatted result ─────────────────────────────────────────────────
  const renderResult = () => {
    if (!result) return null;

    // If the API returned a plain string, show it directly
    if (typeof result.data === "string") {
      return <p className="ai-result-text">{result.data}</p>;
    }

    // If it returned an object with a summary / recommendation / report field
    const { summary, recommendation, report, message, ...rest } = result.data;

    return (
      <>
        {summary && (
          <div className="ai-result-section">
            <span className="ai-result-label">Summary</span>
            <p className="ai-result-text">{summary}</p>
          </div>
        )}
        {recommendation && (
          <div className="ai-result-section">
            <span className="ai-result-label">Recommendation</span>
            <p className="ai-result-text">{recommendation}</p>
          </div>
        )}
        {report && (
          <div className="ai-result-section">
            <span className="ai-result-label">Report</span>
            <p className="ai-result-text">{report}</p>
          </div>
        )}
        {message && (
          <div className="ai-result-section">
            <span className="ai-result-label">Message</span>
            <p className="ai-result-text">{message}</p>
          </div>
        )}
        {Object.keys(rest).length > 0 && (
          <div className="ai-result-section">
            <span className="ai-result-label">Raw Response</span>
            <pre className="ai-result-raw">{JSON.stringify(rest, null, 2)}</pre>
          </div>
        )}
      </>
    );
  };

  return (
    <div className="ai-panel">
      {/* Header */}
      <div className="ai-panel-header">
        <span className="ai-panel-icon">✦</span>
        <h2 className="ai-panel-title">AI Assistant</h2>
        <span className="ai-panel-badge">Powered by AI</span>
      </div>

      <p className="ai-panel-description">
        Select an action below to get AI‑powered insights for this diagram.
      </p>

      {/* Action Buttons */}
      <div className="ai-panel-actions">
        <button
          id="ai-btn-describe"
          className={`ai-btn ai-btn--describe ${activeAction === "Describe" ? "ai-btn--loading" : ""}`}
          onClick={() => callAI("describe", "Describe")}
          disabled={loading}
          aria-busy={activeAction === "Describe"}
        >
          {activeAction === "Describe" ? (
            <span className="ai-btn-spinner" />
          ) : (
            <span className="ai-btn-icon">🔍</span>
          )}
          Describe
        </button>

        <button
          id="ai-btn-recommend"
          className={`ai-btn ai-btn--recommend ${activeAction === "Recommend" ? "ai-btn--loading" : ""}`}
          onClick={() => callAI("recommend", "Recommend")}
          disabled={loading}
          aria-busy={activeAction === "Recommend"}
        >
          {activeAction === "Recommend" ? (
            <span className="ai-btn-spinner" />
          ) : (
            <span className="ai-btn-icon">💡</span>
          )}
          Recommend
        </button>

        <button
          id="ai-btn-report"
          className={`ai-btn ai-btn--report ${activeAction === "Generate Report" ? "ai-btn--loading" : ""}`}
          onClick={() => callAI("generate-report", "Generate Report")}
          disabled={loading}
          aria-busy={activeAction === "Generate Report"}
        >
          {activeAction === "Generate Report" ? (
            <span className="ai-btn-spinner" />
          ) : (
            <span className="ai-btn-icon">📄</span>
          )}
          Generate Report
        </button>
      </div>

      {/* Loading state */}
      {loading && (
        <div className="ai-loading">
          <div className="ai-loading-dots">
            <span /><span /><span />
          </div>
          <p>Processing with AI — this may take a moment…</p>
        </div>
      )}

      {/* Error state */}
      {error && !loading && (
        <div className="ai-error" role="alert">
          <span className="ai-error-icon">⚠️</span>
          <p>{error}</p>
          <button className="ai-error-dismiss" onClick={() => setError(null)}>
            Dismiss
          </button>
        </div>
      )}

      {/* Result */}
      {result && !loading && (
        <div className="ai-result" role="region" aria-label="AI response">
          <div className="ai-result-header">
            <span className="ai-result-action-tag">{result.action}</span>
            <button
              className="ai-result-clear"
              onClick={() => setResult(null)}
              title="Clear result"
            >
              ✕
            </button>
          </div>
          <div className="ai-result-body">{renderResult()}</div>
        </div>
      )}
    </div>
  );
}

export default AIPanel;
