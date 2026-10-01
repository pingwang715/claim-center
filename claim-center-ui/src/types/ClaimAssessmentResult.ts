export interface ClaimAssessmentResult {
  riskScore: number;
  summary: string;
  fraudIndicators: string[];
  recommendedAction: string;
}
