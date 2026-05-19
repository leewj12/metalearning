import React, { useEffect } from "react";

const toastStyle = {
  position: "fixed",
  bottom: "2rem",
  left: "50%",
  transform: "translateX(-50%)",
  background: "#323232",
  color: "#fff",
  padding: "0.75rem 1.5rem",
  borderRadius: "8px",
  fontSize: "0.9rem",
  zIndex: 9999,
  boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
  pointerEvents: "none",
};

export default function ComingSoonToast({ visible, onHide }) {
  useEffect(() => {
    if (!visible) return;
    const t = setTimeout(onHide, 2500);
    return () => clearTimeout(t);
  }, [visible, onHide]);

  if (!visible) return null;
  return <div style={toastStyle}>🚧 현재 준비 중인 서비스입니다.</div>;
}
