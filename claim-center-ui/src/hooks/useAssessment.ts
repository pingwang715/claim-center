import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ClaimAssessmentResponse } from "../types/ClaimAssessmentResponse";

const API_BASE = import.meta.env.BASE_URL ?? "";

function authHeaders(): HeadersInit {
  const jwtToken = localStorage.getItem("jwtToken")
  return {
    "Content-Type": "application/json",
    ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
  };
}

export const assessmentKeys = {
  latest: (claimId: number | string) => ["assessment", "latest", claimId] as const,
};

export function useRunAssessment() {
  const queryClient = useQueryClient();

  return useMutation<ClaimAssessmentResponse, Error, number | string>({
    mutationFn: async (claimId: number | string) => {
      const response = await fetch(`${API_BASE}/claims/${claimId}/ai-assessment`,
        {method: "POST", headers: authHeaders(),}
      );

      if (!response.ok) throw new Error(`Assessment failed ${response.status}`);
      return response.json();
    },

    onSuccess: (data, claimId) => {
      queryClient.setQueryData(assessmentKeys.latest(claimId), data);
    },
  });
}

export function useLatestAssessment(
  claimId: number | string | undefined,
  options?: { enabled?: boolean }
) {
  return useQuery<ClaimAssessmentResponse | null, Error>({
    queryKey: assessmentKeys.latest(claimId ?? "none"),
    queryFn: async () => {
      const response = await fetch(`${API_BASE}/claims/${claimId}/ai-assessment/latest`, {
        headers: authHeaders(),
      });

      if (response.status === 404) return null;
      if (!response.ok) throw new Error(`Failed to load assessment (${response.status})`);
      return response.json();
    },

    enabled: claimId != null && (options?.enabled ?? true),
    retry: false,
  })
}
