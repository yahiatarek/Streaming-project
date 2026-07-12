import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Streaming Project",
  description: "Frontend for the streaming microservices project",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
