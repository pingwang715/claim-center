import React from "react";

interface DropdownProps {
  label: string;
  options: string[];
  selectedValue: string;
  handleSort: (value: string) => void;
}

export default function Dropdown({
  label,
  options,
  selectedValue,
  handleSort,
}: DropdownProps) {
  return (
    <div className="flex items-center gap-2 justify-end pr-12 flex-1 font-primary">
      <label className="text-xs font-semibold text-primary">
        {label}
      </label>
      <select
        className="px-2 py-2 text-xs border rounded-md transition border-primary focus:ring focus:ring-dark   focus:outline-none text-gray-800"
        value={selectedValue}
        onChange={(event) => handleSort(event.target.value)}
      >
        {options.map((optionVal, index) => (
          <option key={index} value={optionVal}>
            {optionVal}
          </option>
        ))}
      </select>
    </div>
  );
}
