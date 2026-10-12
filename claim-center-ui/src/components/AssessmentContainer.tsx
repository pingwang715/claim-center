import AssessmentPanel from "./AssessmentPanel";
import { useLatestAssessment, useRunAssessment } from "../hooks/useAssessment";

export default function AssessmentContainer({ claimId }: { claimId: number }) {
  const latest = useLatestAssessment(claimId);
  const run = useRunAssessment();

  return (
    <AssessmentPanel
      assessment={latest.data ?? null}
      loading={run.isPending || latest.isLoading}
      error={run.error?.message ?? latest.error?.message ?? null}
      onRetry={() => run.mutate(claimId)}
    />
  );
}
