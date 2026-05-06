import { format, parseISO } from "date-fns";

export function formatDate(ts:string, pattern: string = "dd MMM yyyy HH:mm:ss"): string {
  return format(parseISO(ts), pattern);
}
