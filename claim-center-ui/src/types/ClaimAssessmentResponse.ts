export interface ClaimAssessmentResponse {
  riskScore: number;
  summary: string;
  fraudIndicators: string[];
  recommendedAction: string;
}
