import React, { useRef, useEffect } from "react";
import PageTitle from "./PageTitle";
import {
  Link,
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

export default function Register(): React.JSX.Element {
  const actionData = useActionData<{ error?: string; success?: boolean }>();
  const navigation = useNavigation();
  const navigate = useNavigate();
  const formRef = useRef<HTMLFormElement>(null);
  const submit = useSubmit();

  const isSubmitting: boolean = navigation.state === "submitting";

  useEffect(() => {
    if (actionData?.success) {
      navigate("/login");
      toast.success("Registration completed successfully. Try login.");
    }
  }, [actionData]);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault();
    const formData = new FormData(formRef.current!);
    if (!validatePasswords(formData)) {
      return;
    }
    submit(formData, { method: "POST" });
  };

  const validatePasswords = (formData: FormData): boolean => {
    const password = formData.get("password") as string;
    const confirmPwd = formData.get("confirmPwd") as string;

    if (password !== confirmPwd) {
      toast.error("Passwords do not match!");
      return false;
    }
    return true;
  };

  const labelStyle = "block text-lg font-semibold text-primary mb-2";
  const textFieldStyle =
    "w-full px-4 py-2 text-base border rounded-md transition border-primary focus:ring focus:ring-dark focus:outline-none text-secondary bg-white placeholder-gray-400";

  return (
    <div className="min-h-[820px] flex items-center justify-center font-primary bg-normalbg">
      <div className="bg-white shadow-md rounded-lg max-w-md w-full px-8 py-6">
        <PageTitle title="Register" />

        <Form
          method="POST"
          ref={formRef}
          onSubmit={handleSubmit}
          className="space-y-6"
        >
          <div className="mt-2">
            <label htmlFor="firstName" className={labelStyle}>
              First Name
            </label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              placeholder="Your First Name"
              required
              minLength={2}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="lastName" className={labelStyle}>
              Last Name
            </label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              placeholder="Your Last Name"
              required
              minLength={2}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="email" className={labelStyle}>
              First Name
            </label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Your Email"
              autoComplete="email"
              required
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="password" className={labelStyle}>
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Your Password"
              required
              autoComplete="new-password"
              minLength={4}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div className="mt-2">
            <label htmlFor="confirmPwd" className={labelStyle}>
              Password
            </label>
            <input
              type="password"
              id="confirmPwd"
              name="confirmPwd"
              placeholder="Confirm Your Password"
              required
              autoComplete="confirm-password"
              minLength={4}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div className="text-center">
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2 justify-center text-white text-xl bg-primary hover:bg-dark rounded-md transition duration-200 mt-6"
            >
              {isSubmitting ? "Registering..." : "Register"}
          </button>
          </div>
        </Form>

        <p className="text-center text-gray-600 mt-4">
          Already have an account?{" "}
          <Link
            to="/login"
            className="text-primary hover:text-dark transition duration-200"
          >
            Login Here
          </Link>
        </p>
      </div>
    </div>
  );
}

interface RegisterResponse {
  success: boolean;
}

interface RegisterActionResult {
  success: boolean;
  errors?: { message: string };
}

export async function RegisterAction({
  request,
}: ActionFunctionArgs): Promise<RegisterActionResult> {
  const data = await request.formData();

  const registerData = {
    firstName: data.get("firstName") as string,
    lastName: data.get("lastName") as string,
    email: data.get("email") as string,
    password: data.get("password") as string,
  };

  try {
    const response = await apiClient.post<RegisterResponse>(
      "/auth/register",
      registerData,
    );
    return { success: true };
  } catch (err) {
    const error = err as AxiosError<{ message: string; errorMessage?: string }>;
    if (error.response?.status === 400) {
      return { success: false, errors: error.response?.data };
    }
    throw new Response(
      error.response?.data?.errorMessage ||
        error.message ||
        "Failed to register. Please try again.",
      { status: error.response?.status || 500 },
    );
  }
}
