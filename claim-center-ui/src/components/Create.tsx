import React, { useRef, useEffect } from "react";
import PageTitle from "./PageTitle";
import {
  Form,
  useActionData,
  useNavigation,
  useNavigate,
  useSubmit,
} from "react-router-dom";
import { toast } from "react-toastify";
import apiClient from "../api/apiClient";
import type { ActionFunctionArgs } from "react-router-dom";
import type { AxiosError } from "axios";
import type { PolicyType } from "../types/policyTypes";

export default function Create(): React.JSX.Element {
  const actionData = useActionData<{ error?: string; success?: boolean }>();
  const navigation = useNavigation();
  const navigate = useNavigate();
  const formRef = useRef<HTMLFormElement>(null);
  const submit = useSubmit();

  const isSubmitting: boolean = navigation.state === "submitting";

  useEffect(() => {
    if (actionData?.success) {
      navigate("/claims");
      toast.success(
        "Claim submitted successfully. You can track the status later.",
      );
    }
  }, [actionData]);

  const policyTypes: { label: string; value: PolicyType }[] = [
    { label: "Health", value: "health" },
    { label: "Car", value: "car" },
    { label: "Travel", value: "travel" },
    { label: "Pet", value: "pet" },
    { label: "Property", value: "property" },
  ];

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault();
    const formData = new FormData(formRef.current!);
    submit(formData, { method: "POST" });
  };

  const labelStyle = "block text-sm font-semibold text-primary mb-2";
  const textFieldStyle =
    "w-full px-4 py-2 text-base border rounded-md transition border-primary focus:ring focus:ring-dark focus:outline-none text-secondary bg-white placeholder-gray-400";

  return (
    <div className="min-h-[810px] flex justify-center bg-normalbg py-4 px-4 font-primary">
      <div className="max-w-xl w-full px-8 py-6">
        <PageTitle title="Create a claim" />
        <Form
          method="POST"
          ref={formRef}
          className="space-y-6"
          onSubmit={handleSubmit}
        >
          <div className="mt-2">
            <label htmlFor="title" className={labelStyle}>
              Claim Title
            </label>
            <input
              type="text"
              id="title"
              name="title"
              placeholder="Your Title"
              required
              minLength={5}
              maxLength={100}
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="description" className={labelStyle}>
              Description
            </label>
            <textarea
              id="description"
              name="description"
              placeholder="Description"
              required
              minLength={10}
              maxLength={500}
              rows={6}
              className={`${textFieldStyle} resize-y min-h-[120px]`}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="claimedAmount" className={labelStyle}>
              Claimed Amount
            </label>
            <input
              type="number"
              id="claimedAmount"
              name="claimedAmount"
              placeholder="0.00"
              required
              min={0}
              max={9999999.99}
              step={0.01}
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label className={labelStyle}>Policy Type</label>
            <div className="flex flex-wrap gap-6 mt-1">
              {policyTypes.map(({ label, value }) => (
                <label
                  key={value}
                  className="flex items-center gap-2 cursor-pointer text-sm"
                >
                  <input
                    type="radio"
                    name="policyType"
                    value={value}
                    required
                    className="accent-amber-700"
                  />
                  <span>{label}</span>
                </label>
              ))}
            </div>
          </div>

          <div className="text-center">
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2 text-white text-lg bg-primary hover:bg-dark rounded-md transition duration-200 mt-6"
              >
              {isSubmitting ? "Submitting..." : "Submit"}
            </button>
          </div>
        </Form>
      </div>
    </div>
  );
}

interface CreateResponse {
  success: boolean;
}

interface CreateActionResult {
  success: boolean;
  errors?: { message: string };
}

export async function CreateAction({
  request,
}: ActionFunctionArgs): Promise<CreateActionResult> {
  const data = await request.formData();

  const createData = {
    title: data.get("title") as string,
    description: data.get("description") as string,
    claimedAmount: parseFloat(data.get("claimedAmount") as string),
    type: data.get("policyType") as PolicyType,
  };

  try {
    const response = await apiClient.post<CreateResponse>("/claims", createData, {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwtToken")}` },
    });
    return { success: true };
  } catch (err) {
    const error = err as AxiosError<{ message: string; errorMessage?: string }>;
    if (error.response?.status === 400) {
      return { success: false, errors: error.response?.data };
    }
    throw new Response(
      error.response?.data?.errorMessage ||
        error.message ||
        "Failed to submit your claim. Please try again.",
      { status: error.response?.status || 500 },
    );
  }

}
