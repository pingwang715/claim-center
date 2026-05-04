import React from "react";

interface SearchBoxProps {
  label: string;
  placeholder: string;
  value: string;
  handleSearch: (value: string) => void;
}

export default function SearchBox({ label, placeholder, value, handleSearch }: SearchBoxProps) {
  return (
    <div className="flex items-center gap-3 pl-4 flex-1 font-primary">
      <label className="text-xs font-semibold text-primary dark:text-light">
        {label}
      </label>
      <input
        type="text"
        className="px-2 py-2 text-xs border rounded-md transition border-primary focus:ring focus:ring-dark  focus:outline-none text-gray-800"
        placeholder={placeholder}
        value={value}
        onChange={(event) => handleSearch(event.target.value)}
      />
    </div>
  );
}
