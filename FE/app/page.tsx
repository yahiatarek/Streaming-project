"use client";

import Hls from "hls.js";
import Link from "next/link";
import { useEffect, useRef, useState } from "react";

const playbackUrl =
  "https://res.cloudinary.com/dvaebribq/video/upload/sp_auto/v1785346239/my-streaming-app/xkmy8mpajuyhikgqwb17.m3u8";
const fallbackMp4Url =
  "https://res.cloudinary.com/dvaebribq/video/upload/v1785346239/my-streaming-app/xkmy8mpajuyhikgqwb17.mp4";

export default function Home() {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [status, setStatus] = useState("Preparing player...");

  useEffect(() => {
    const video = videoRef.current;

    if (!video) {
      return;
    }

    setError(null);

    const isSafari =
      /^((?!chrome|android).)*safari/i.test(navigator.userAgent) ||
      navigator.vendor.includes("Apple");

    if (isSafari && video.canPlayType("application/vnd.apple.mpegurl")) {
      video.src = playbackUrl;
      video.load();
      setStatus("Using native HLS playback.");
      return;
    }

    if (!Hls.isSupported()) {
      video.src = fallbackMp4Url;
      video.load();
      setStatus("HLS is not supported. Using MP4 fallback.");
      return;
    }

    const hls = new Hls({
      backBufferLength: 0,
      capLevelToPlayerSize: true,
      enableWorker: true,
      lowLatencyMode: false,
      maxBufferLength: 10,
      maxBufferSize: 15 * 1000 * 1000,
      maxMaxBufferLength: 20,
      startLevel: 0,
    });

    hls.attachMedia(video);

    hls.on(Hls.Events.MEDIA_ATTACHED, () => {
      setStatus("Loading HLS manifest...");
      hls.loadSource(playbackUrl);
    });

    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      setStatus("HLS stream loaded. Press play.");
    });
    hls.on(Hls.Events.LEVEL_SWITCHED, (_event, data) => {
      const level = hls.levels[data.level];

      setStatus(
        `Quality changed: ${level.width}x${level.height}, ${Math.round(
          level.bitrate / 1000,
        )} kbps`,
      );
    });

    hls.on(Hls.Events.LEVEL_LOADED, () => {
      setStatus("HLS level loaded. Press play.");
    });

    hls.on(Hls.Events.ERROR, (_event, data) => {
      console.error("HLS error", data);

      if (!data.fatal) {
        return;
      }

      if (data.details === Hls.ErrorDetails.BUFFER_FULL_ERROR) {
        hls.stopLoad();
        hls.destroy();
        video.src = fallbackMp4Url;
        video.load();
        setStatus("Browser buffer is full. Using MP4 fallback.");
        return;
      }

      if (data.type === Hls.ErrorTypes.MEDIA_ERROR) {
        setStatus("Recovering from media error...");
        hls.recoverMediaError();
        return;
      }

      hls.destroy();
      video.src = fallbackMp4Url;
      video.load();
      setStatus("HLS failed. Using MP4 fallback.");
      setError(`HLS playback failed: ${data.type}`);
    });

    return () => {
      hls.destroy();
    };
  }, []);

  return (
    <main className="page">
      <section className="hero">
        <p className="eyebrow">Streaming Project</p>
        <h1>Video playback</h1>
        <p className="description">
          This player uses the Cloudinary HLS playback URL. The player chooses
          the best available quality automatically.
        </p>
        <Link className="text-link" href="/upload">
          Upload a new video
        </Link>

        <video
          ref={videoRef}
          className="video-player"
          controls
          playsInline
          preload="metadata"
          onCanPlay={() => setStatus("Video can play. Press play.")}
          onError={() => {
            setError("The browser could not load this video source.");
          }}
        />

        <p className="player-status">{status}</p>
        {error && <p className="error">{error}</p>}
      </section>
    </main>
  );
}
