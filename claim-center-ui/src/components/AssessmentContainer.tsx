import { useState } from "react";
import AssessmentPanel from "./AssessmentPanel";
import { useLatestAssessment, useRunAssessment } from "../hooks/useAssessment";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRobot } from "@fortawesome/free-solid-svg-icons";

type Source = "created" | "latest" | null;

export default function AssessmentContainer({ claimId }: { claimId: number | string | undefined }) {
  const [source, setSource] = useState<Source>(null);

  const latest = useLatestAssessment(claimId, { enabled: false });
  const run = useRunAssessment();

  const handleCreate = () => {
    if (claimId == null) return;
    setSource("created");
    run.mutate(claimId);
  };

  const handleRetrieve = () => {
    if (claimId == null) return;
    setSource("latest");
    latest.refetch();
  }

  const assessment =
    source === "created" ? run.data ?? null
    : source === "latest" ? latest.data ?? null
    : null;

  const busy = run.isPending || latest.isFetching;

  const error =
    source === "created" ? run.error?.message ?? null
    : source === "latest" ? latest.error?.message ?? null
    : null;

  const retry = source === "latest" ? handleRetrieve : handleCreate;

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-xl p-5 shadow-md m-4">
      <div className="text-primary text-lg font-semibold text-center mx-2 my-4">
        <FontAwesomeIcon icon={faRobot} className="mx-2"/>
        Try Your AI Assisstant
      </div>
      <div role="toolbar" aria-label="Assessment actions" className="flex gap-3 items-center my-4">

        <button type="button" onClick={handleCreate} disabled={busy} className="w-full bg-primary text-white rounded-xl p-2 hover:opacity-80">
          {run.isPending ? "Creating..." : "Create assessment"}
        </button>

        <button type="button" onClick={handleRetrieve} disabled={busy} className="w-full bg-secondary text-white rounded-xl p-2 hover:opacity-80">
          {latest.isFetching ? "Loading..." : "Get latest assessment"}
        </button>
      </div>

      <AssessmentPanel
        assessment={assessment}
        loading={busy}
        error={error}
        onRetry={retry}
      />
    </div>
  );
}
