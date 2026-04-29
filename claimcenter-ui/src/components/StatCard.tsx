import React from "react";

interface StatCardProps {
  label: string;
  value: string | number;
  variant?: "default" | "warning" | "alert" | "success";
  small?: boolean;
}

const VARIANT_STYLES: Record<NonNullable<StatCardProps["variant"]>, string> = {
  default: "text-gray-900",
  warning: "text-amber-600",
  alert: "text-red-600",
  success: "text-green-600",
};

export const StatCard: React.FC<StatCardProps> = ({
  label,
  value,
  variant = "default",
  small = false,
}) => (
  <div className="border border-gray-200 rounded-b-lg shadow-md px-8 py-4 ">
    <p className="text-xs text-gray-500 mb-1">{label}</p>
    <p
      className={`font-medium ${small ? "text-base" : "text-xl"} ${VARIANT_STYLES[variant]}`}
    >
      {value}
    </p>
  </div>
);
