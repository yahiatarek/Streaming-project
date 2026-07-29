"use client";

import { FormEvent, useMemo, useState } from "react";

type UploadSignature = {
  timestamp: number;
  folder: string;
  api_key: string;
  cloud_name: string;
  upload_url: string;
  signature: string;
};

type CloudinaryUploadResponse = {
  public_id: string;
  secure_url: string;
  playback_url?: string;
  original_filename: string;
  format: string;
  resource_type: string;
  bytes: number;
  duration?: number;
  width?: number;
  height?: number;
};

type SavedVideo = {
  id: number;
  title: string;
  storagePath: string;
  status: string;
};

const apiBaseUrl = "/api/backend";

export default function UploadPage() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [status, setStatus] = useState("Choose a video file.");
  const [error, setError] = useState<string | null>(null);
  const [savedVideo, setSavedVideo] = useState<SavedVideo | null>(null);
  const [cloudinaryResponse, setCloudinaryResponse] =
    useState<CloudinaryUploadResponse | null>(null);

  const fileSizeMb = useMemo(() => {
    if (!file) {
      return null;
    }

    return (file.size / 1024 / 1024).toFixed(2);
  }, [file]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!file) {
      setError("Please choose a video file first.");
      return;
    }

    setError(null);
    setSavedVideo(null);
    setCloudinaryResponse(null);

    try {
      setStatus("Requesting Cloudinary upload signature...");
      const signatureResponse = await fetch(
        `${apiBaseUrl}/videos/upload-signature`,
      );

      if (!signatureResponse.ok) {
        throw new Error("Could not get upload signature from backend.");
      }

      const uploadSignature =
        (await signatureResponse.json()) as UploadSignature;

      const cloudinaryFormData = new FormData();
      cloudinaryFormData.append("file", file);
      cloudinaryFormData.append("api_key", uploadSignature.api_key);
      cloudinaryFormData.append(
        "timestamp",
        String(uploadSignature.timestamp),
      );
      cloudinaryFormData.append("signature", uploadSignature.signature);
      cloudinaryFormData.append("folder", uploadSignature.folder);

      setStatus("Uploading video directly to Cloudinary...");
      const cloudinaryUploadResponse = await fetch(
        uploadSignature.upload_url,
        {
          method: "POST",
          body: cloudinaryFormData,
        },
      );

      if (!cloudinaryUploadResponse.ok) {
        const body = await cloudinaryUploadResponse.text();
        throw new Error(`Cloudinary upload failed: ${body}`);
      }

      const uploadedVideo =
        (await cloudinaryUploadResponse.json()) as CloudinaryUploadResponse;
      setCloudinaryResponse(uploadedVideo);

      setStatus("Saving video metadata in backend...");
      const metadataResponse = await fetch(`${apiBaseUrl}/videos/upload`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          title: title || uploadedVideo.original_filename,
          description,
          originalFileName: `${uploadedVideo.original_filename}.${uploadedVideo.format}`,
          storagePath: uploadedVideo.public_id,
          contentType: `${uploadedVideo.resource_type}/${uploadedVideo.format}`,
          sizeInBytes: uploadedVideo.bytes,
          durationInSeconds: uploadedVideo.duration
            ? Math.round(uploadedVideo.duration)
            : undefined,
          width: uploadedVideo.width,
          height: uploadedVideo.height,
        }),
      });

      if (!metadataResponse.ok) {
        const body = await metadataResponse.text();
        throw new Error(`Backend metadata save failed: ${body}`);
      }

      const saved = (await metadataResponse.json()) as SavedVideo;
      setSavedVideo(saved);
      setStatus("Upload finished.");
    } catch (exception) {
      setStatus("Upload failed.");
      setError(
        exception instanceof Error
          ? exception.message
          : "Unknown upload error.",
      );
    }
  }

  return (
    <main className="page">
      <section className="hero">
        <p className="eyebrow">Streaming Project</p>
        <h1>Upload video</h1>
        <p className="description">
          The file is uploaded directly to Cloudinary. The backend stores only
          the returned video metadata.
        </p>

        <form className="upload-form" onSubmit={handleSubmit}>
          <label className="field">
            <span>Title</span>
            <input
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Big Buck Bunny"
            />
          </label>

          <label className="field">
            <span>Description</span>
            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Optional video description"
              rows={4}
            />
          </label>

          <label className="field">
            <span>Video file</span>
            <input
              accept="video/*"
              type="file"
              onChange={(event) => {
                setFile(event.target.files?.[0] ?? null);
                setSavedVideo(null);
                setCloudinaryResponse(null);
                setError(null);
              }}
            />
          </label>

          {fileSizeMb && (
            <p className="hint">
              Selected file: {file?.name} ({fileSizeMb} MB)
            </p>
          )}

          <button className="button" disabled={!file} type="submit">
            Upload
          </button>
        </form>

        <p className="player-status">{status}</p>
        {error && <p className="error">{error}</p>}

        {cloudinaryResponse && (
          <div className="result-card">
            <h2>Cloudinary result</h2>
            <p>
              <strong>public_id:</strong> {cloudinaryResponse.public_id}
            </p>
            <p>
              <strong>secure_url:</strong> {cloudinaryResponse.secure_url}
            </p>
            {cloudinaryResponse.playback_url && (
              <p>
                <strong>playback_url:</strong>{" "}
                {cloudinaryResponse.playback_url}
              </p>
            )}
          </div>
        )}

        {savedVideo && (
          <div className="result-card">
            <h2>Backend result</h2>
            <p>
              <strong>id:</strong> {savedVideo.id}
            </p>
            <p>
              <strong>storagePath:</strong> {savedVideo.storagePath}
            </p>
            <p>
              <strong>status:</strong> {savedVideo.status}
            </p>
          </div>
        )}
      </section>
    </main>
  );
}
