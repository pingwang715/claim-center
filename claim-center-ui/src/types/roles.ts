export type UserRole = "claimant" | "adjuster" | "manager";

export interface RoleAction {
  label: string;
  apiCall: () => Promise<void>;
  buttonStyle: string;
}
