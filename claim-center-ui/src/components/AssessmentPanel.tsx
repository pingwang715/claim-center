import React from "react";
import type { ClaimAssessmentResponse } from "../types/ClaimAssessmentResponse";

interface Props {
  assessment: ClaimAssessmentResponse | null;
  loading?: boolean;
  error?: string | null;
  onRetry?: () => void;
}

const card = "max-w-xl rounded-lg border border-slate-200 bg-white p-5 text-slate-900";
const heading = "mb-1 text-[15px] font-semibold";
const muted = "text-sm text-slate-500";

export default function AssessmentPanel({ assessment, loading, error, onRetry }: Props){
  if (loading) {
    return (
      <section className={card} aria-busy="true" aria-live="polite">
        <p className={muted}>Assessming this claim. This can take a few seconds.</p>
      </section>
    );
  }

  if (error) {
    return (
      <section className={card} role="alert">
        <p className={muted}>The assessment could not be completed: {error}</p>
      {onRetry && (
        <button type="button" onClick={onRetry} className="mt-3 rounded-md bg-slate-900 px-3.5 py-1.5 text-sm text-white hover:bg-slate-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-slate-900">
          Run assessment again
        </button>
      )}
      </section>
    );
  }

  if (!assessment) {
    return (
      <section className={card}>
        <p className={muted}>No assessment yet. Run one to see a risk score and recommendation.</p>
      </section>
    );
  }

  const indicators = assessment.fraudIndicators ?? [];

  return (
    <section className={card} aria-labelledby="ap-title">
      <header className="mb-4">
        <h2 id="ap-title" className="text-lg font-semibold">AI Assessment</h2>
        <p className={muted}>Advisory only. The adjuster should make the final decision.</p>
      </header>

      <div className="mb-4">
        <h3 className={heading}>Risk Score</h3>
        <p>{assessment.riskScore}</p>
      </div>

      <div className="mb-4">
        <h3 className={heading}>Summary</h3>
        <p>{assessment.summary}</p>
      </div>

      <div className="mb-4">
        <h3 className={heading}>Fraud Indicators ({indicators.length})</h3>
        {indicators.length === 0 ? (
          <p className={muted}>No fraud indicators were flagged.</p>
        ) : (
          <ul className="list-disc space-y-1 pl-5">
            {indicators.map((item, i) => (
              <li key={`${i}-${item}`}>{item}</li>
            ))}
          </ul>
        )}
      </div>

      <div className="rounded-r-md border-1-4 bg-slate-50 px-4 py-3">
        <h3 className={heading}>Recommended Action</h3>
        <p>{assessment.recommendedAction}</p>
      </div>


    </section>
  )

}
