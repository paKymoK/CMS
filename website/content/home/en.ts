import type { HomeContent } from "./types";

/**
 * Redesigned 2026-09-16 to match the Claude Design homepage handoff.
 * footerNav is untouched (the header/footer shell keeps its existing content
 * per this pass's scope). Everything else below is transcribed verbatim from
 * the handoff's prototype — per the handoff's own note, this text content
 * (service names, office records, case-study titles/dates, insight captions,
 * testimonial copy) is real, client-approved copy, not placeholder (only
 * imagery is placeholder pending real assets from the client).
 * primaryNav/navSections used to live here too — now hardcoded in
 * content/nav.ts since they never change per-region.
 */
export const homeContentEn: HomeContent = {
  services: [
    { name: "AI & Digital Strategy Advisory" },
    { name: "Data & AI Solutions" },
    { name: "Digital & Cloud Solutions" },
    { name: "Enterprise Solutions" },
    { name: "Infrastructure & Managed Services" },
    { name: "Cybersecurity" },
    { name: "Software Engineering" },
  ],
  stats: [
    { value: "32+", label: "Years Of Experience", note: "Part of CMC Corporation, founded 1993." },
    { value: "30+", label: "Countries", note: "Delivery presence across three regions." },
    { value: "300+", label: "Global Clients", note: "Bosch, Honda, AIA, IBM, LINE and more." },
    { value: "35+", label: "Business Partners", note: "AWS, Google Cloud, UiPath, Salesforce." },
    { value: "3000+", label: "Employees", note: "Across delivery centres worldwide." },
  ],
  // lat/lon/flag/address extracted verbatim from the prototype's initGlobe().
  offices: [
    {
      city: "Hanoi Office (Headquarter)",
      lat: 21.03,
      lon: 105.78,
      flag: "#da251d",
      big: true,
      address: "7 - 10F, CMC Tower, 11 Duy Tan Street, Cau Giay\nWard, Hanoi 100000, Vietnam",
    },
    {
      city: "Ho Chi Minh City Office",
      lat: 10.78,
      lon: 106.7,
      flag: "#da251d",
      address: "Delivery centre · 1200+ engineers\nDistrict 1, Ho Chi Minh City, Vietnam",
    },
    {
      city: "Osaka Office",
      lat: 34.69,
      lon: 135.5,
      flag: "#bc002d",
      big: true,
      address: "Mosaiwaki Frontier Bldg. 1F, 1-12-23\nSakaedori-Machi, Kita-ku, Osaka, Japan",
    },
    {
      city: "Seoul Office",
      lat: 37.56,
      lon: 126.98,
      flag: "#0047a0",
      address: "Korea market & partnerships\nGangnam-gu, Seoul, Korea",
    },
    {
      city: "Singapore Office",
      lat: 1.29,
      lon: 103.85,
      flag: "#ed2939",
      address: "APAC hub · Sales & advisory\nDowntown Core, Singapore",
    },
    {
      city: "Sydney Office",
      lat: -33.87,
      lon: 151.21,
      flag: "#00247d",
      address: "ANZ market\nSydney CBD, NSW, Australia",
    },
    {
      city: "San Jose Office",
      lat: 37.34,
      lon: -121.89,
      flag: "#b22234",
      address: "Silicon Valley\nSan Jose, California, USA",
    },
    {
      city: "Frankfurt Office",
      lat: 50.11,
      lon: 8.68,
      flag: "#1a1a1a",
      address: "DACH market\nFrankfurt am Main, Germany",
    },
  ],
  // Titles/dates/categories transcribed verbatim from the prototype markup.
  caseStudies: [
    {
      title: "38% Higher Productivity & 40% Faster QA With Agentic AI For Intrusion Detection Software",
      date: "Jun 2, 2026",
      category: "Agentic AI Software Delivery",
    },
    {
      title: "15% Increase In Revenue With AI Energy Trading System",
      date: "Jun 2, 2026",
      category: "AI-Powered Trading & Forecasting",
    },
    {
      title: "30% Ticket Return Reduced With Smart Field Service Software Using Azure OpenAI",
      date: "Jun 2, 2026",
      category: "AI-Powered Field Operations",
    },
    {
      title: "Beyond Innovation: Revolv CTO On Why Trust And “Unbreakable Connectivity” Won The Deal",
      date: "Jun 2, 2026",
      category: "Software Engineering",
    },
  ],
  // Captions transcribed verbatim from the prototype's initIns().
  insights: [
    { title: "Vietnam-China AI and Digital Economy Dialogue 2026: A New Chapter for Bilateral Tech Cooperation" },
    { title: "AI Workshop Series: Turning Pilots Into Production Across The Enterprise" },
    { title: "Bridging Talent And Technology: Our New Delivery Centre In Ho Chi Minh City" },
    { title: "AI Business Insight Night: What Boards Are Asking About AI In 2026" },
  ],
  awards: [
    { name: "Award 1", logo: "/images/awards/award-1.png" },
    { name: "Award 2", logo: "/images/awards/award-2.png" },
    { name: "Award 3", logo: "/images/awards/award-3.png" },
    { name: "Award 4", logo: "/images/awards/award-4.png" },
    { name: "Award 5", logo: "/images/awards/award-5.png" },
  ],
  certifications: [
    { name: "ISO 9001", logo: "/images/certs/cert-9001.png" },
    { name: "ISO 27001", logo: "/images/certs/cert-iso27001.png" },
    { name: "ITIL", logo: "/images/certs/cert-itil.png" },
    { name: "PCI-DSS", logo: "/images/certs/cert-pcidss.svg" },
    { name: "AWS Advanced Tier Services", logo: "/images/certs/cert-aws-tier.png" },
  ],
  partners: [
    { name: "ITIL", logo: "/images/partners/partner-itil.png" },
    { name: "AWS", logo: "/images/partners/partner-aws.svg" },
    { name: "Google Cloud", logo: "/images/partners/partner-google-cloud.webp" },
    { name: "UiPath", logo: "/images/partners/partner-uipath.png" },
    { name: "ISTQB", logo: "/images/partners/partner-istqb.png" },
  ],
  // Transcribed verbatim from the prototype's initTestimonials() data array.
  testimonials: [
    {
      name: "Mr. Nick Benjamin",
      title: "CTO",
      company: "envato",
      quote: "CMC Global’s happy culture shows up in different ways. One is that CMC Global is very dedicated and they walk the extra mile.",
      flankLogos: [
        { name: "northstar", color: "#121212" },
        { name: "RSUPPORT", color: "#b91c22" },
      ],
    },
    {
      name: "Ms. Elena Rojas",
      title: "VP Engineering",
      company: "northstar",
      quote: "They took our roadmap seriously from week one. The team scaled from four engineers to eighteen without a dip in delivery quality.",
      flankLogos: [
        { name: "RSUPPORT", color: "#b91c22" },
        { name: "envato", color: "#0b63c5" },
      ],
    },
    {
      name: "Mr. Jun Park",
      title: "Head of Product",
      company: "RSUPPORT",
      quote: "What impressed us was the handover. Documentation, tests, runbooks — everything was where it should be when we took it in-house.",
      flankLogos: [
        { name: "envato", color: "#0b63c5" },
        { name: "northstar", color: "#121212" },
      ],
    },
    {
      name: "Mr. Hiroshi Tanaka",
      title: "CIO",
      company: "Mitsui",
      quote: "A genuine partner rather than a vendor. They pushed back when our spec was wrong, which is exactly what we needed.",
      flankLogos: [
        { name: "northstar", color: "#121212" },
        { name: "RSUPPORT", color: "#b91c22" },
      ],
    },
  ],
  // Real category labels and real link labels, extracted from the live footer's DOM text.
  footerNav: [
    {
      label: "World-class IT Outsourcing",
      links: [
        "Custom Software Development",
        "Software Maintenance",
        "Legacy Migration",
        "Testing Services",
      ],
    },
    {
      label: "Digital Transformation",
      links: [
        "Cloud Professional Services",
        "Data & Analytics",
        "Artificial Intelligence Solutions",
        "RPA Services",
        "Low Code",
      ],
    },
    {
      label: "CMC Solutions",
      links: [
        "IoT Smart Device – CIVAMs Face",
        "CMC Social Listening",
        "CMC Chatbot",
        "C-ID Reader",
        "C-CA",
        "SOC",
      ],
    },
    {
      label: "Model",
      links: ["Project-based", "Staff Augmentation", "Hybrid", "Results Based"],
    },
    {
      label: "About Us",
      links: ["CMC Global", "CMC Corporation", "Company Profile"],
    },
    {
      label: "Case Studies",
      links: ["Customer Stories", "Industry", "Business Size"],
    },
    {
      label: "Resources",
      links: ["Blog", "Ebooks & Whitepapers", "News & Events"],
    },
    { label: "Careers", links: ["Careers"] },
  ],
};
