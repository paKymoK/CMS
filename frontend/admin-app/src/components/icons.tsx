import type { CSSProperties, ReactNode } from "react";

// Stroke icons drawn the way the website draws its own (see frontend/website/components/ui/DiagonalArrow.tsx):
// 20px viewBox, 1.5 stroke, round caps/joins, currentColor. Replaces @ant-design/icons so the admin's
// iconography matches the landing page. Sized by font-size like antd's icons (1em square).
type IconProps = { style?: CSSProperties; className?: string };

function make(paths: ReactNode) {
  return function Icon({ style, className }: IconProps) {
    return (
      <span role="img" aria-hidden className={`anticon ${className ?? ""}`} style={{ display: "inline-flex", ...style }}>
        <svg
          viewBox="0 0 20 20"
          width="1em"
          height="1em"
          fill="none"
          stroke="currentColor"
          strokeWidth={1.5}
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          {paths}
        </svg>
      </span>
    );
  };
}

export const ArrowRightIcon = make(<path d="M4 10h12M11 5l5 5-5 5" />);
export const PlusIcon = make(<path d="M10 4v12M4 10h12" />);
export const EditIcon = make(<path d="M3.5 16.5l.8-3.6L13.6 3.6a1.6 1.6 0 0 1 2.3 0l.5.5a1.6 1.6 0 0 1 0 2.3L7.1 15.7l-3.6.8zM12 5.3l2.7 2.7" />);
export const DeleteIcon = make(<path d="M3.5 5.5h13M8 5.5V3.5h4v2M5.5 5.5l.7 11h7.6l.7-11M8.5 9v4.5M11.5 9v4.5" />);
export const UploadIcon = make(<path d="M10 13V3.5M6.5 7L10 3.5 13.5 7M3.5 13v3h13v-3" />);
export const PictureIcon = make(
  <>
    <rect x="3" y="4" width="14" height="12" rx="1.5" />
    <circle cx="7.5" cy="8.5" r="1.2" />
    <path d="M3.5 14l4-3.5 3 2.5 2.5-2 3.5 3" />
  </>,
);
export const MenuIcon = make(<path d="M3.5 5.5h13M3.5 10h13M3.5 14.5h13" />);
export const LogoutIcon = make(<path d="M8 3.5H4.5v13H8M8 10h8.5M13.5 6.5L17 10l-3.5 3.5" />);
export const InboxIcon = make(<path d="M3 11l2-6.5h10L17 11M3 11v5h14v-5M3 11h4.5l.8 2h3.4l.8-2H17" />);
export const EyeIcon = make(
  <>
    <path d="M1.8 10S5 4.5 10 4.5 18.2 10 18.2 10 15 15.5 10 15.5 1.8 10 1.8 10z" />
    <circle cx="10" cy="10" r="2.4" />
  </>,
);
export const CloseIcon = make(<path d="M5 5l10 10M15 5L5 15" />);
export const ChevronRightIcon = make(<path d="M7.5 4.5L13 10l-5.5 5.5" />);
export const ChevronsLeftIcon = make(<path d="M9.5 5L4.5 10l5 5M15.5 5l-5 5 5 5" />);
export const ClockIcon = make(<path d="M10 17a7 7 0 1 0 0-14 7 7 0 0 0 0 14zM10 6v4l2.5 1.5" />);
