import type { HomeContent } from "./types";

/**
 * English homepage content. Text (service names, office records, case-study titles/dates, insight
 * captions, testimonial copy) is real, client-approved copy; only imagery is placeholder.
 * Primary nav and section nav live in content/nav.ts.
 */
export const homeContentEn: HomeContent = {
  // Text-only stand-ins for the CMS-managed hero/assistant content, used only when the CMS is
  // unreachable (imagery comes from the CMS media library, so the fallback has none).
  sections: {
    section_intro: [
      {
        section: "whatwedo",
        eyebrow: "What We Do",
        title: "Architecting Your Next-Gen Digital Core",
        body: "We accelerate your digital evolution by blending advanced AI engineering, robust cloud infrastructure, and deep domain expertise.",
      },
      { section: "testimonials", eyebrow: "Testimonials", title: "Hear More from Our Clients" },
      {
        section: "global",
        eyebrow: "Global Delivery",
        title:
          "Seamless global presence. Non-stop innovation.\nWe accelerate the digital evolution across every time zone",
      },
      {
        section: "cases",
        eyebrow: "Case Studies",
        title: "Stories of Transformation",
        kicker: "Latest Case Study",
        linkLabel: "Explore all Case Studies",
        linkUrl: "/case-studies",
      },
      {
        section: "insights",
        eyebrow: "Insights",
        title: "Building Tomorrow Together",
        linkLabel: "Explore all Insights",
        linkUrl: "/insights",
      },
      {
        section: "recognition",
        title: "Global Recognition for Innovative Engineering",
        body: "Our technical expertise and proven business impact earn us industry-leading awards and elite credentials.",
      },
      { section: "contact", title: "Maximize Your ROI in the Cloud\nLet's Chat!" },
    ],
    feature_band: [
      {
        title: "Your Extended Tech Workforce",
        body: "Expand your engineering bandwidth with flexible, cross-functional tech teams that seamlessly align with your workflows and scale as your goals evolve.",
        ctaLabel: "Meet our experts",
        ctaUrl: "#contact",
      },
    ],
    site_footer: [
      {
        title: "Let's connect",
        ctaLabel: "Get in touch",
        ctaUrl: "#contact",
        copyright: "©2026 CMC Global. All rights reserved",
        privacyLabel: "Privacy & Policy",
        privacyUrl: "#",
        termsLabel: "Terms & Condition",
        termsUrl: "#",
      },
    ],
    assistant_band: [
      {
        title: "The Intelligent Way To Know More About CMC",
        greeting: "Welcome to CMC Global. What brings you here today?",
        welcome:
          "Thank you for checking out our services. If you don’t mind me asking, what brings you to CMC Global today?",
        suggestions: ["Tell me about CMC", "What service do you have?", "Show me your AI Capabilities"],
      },
    ],
  },
  services: [
    {
      name: "AI & Digital Strategy Advisory",
      description:
        "Align your vision with execution through strategic roadmaps that de-risk your digital transformation and accelerate time-to-market.",
    },
    {
      name: "Data & AI Solutions",
      description:
        "Standardize complex data silos into actionable intelligence to automate operations and unlock new revenue streams.",
    },
    {
      name: "Digital & Cloud Solutions",
      description:
        "Migrate and modernize your infrastructure with scalable, high-performance cloud environments built for ultimate business agility.",
    },
    {
      name: "Enterprise Solutions",
      description:
        "Streamline core business operations by seamlessly integrating tailored ERP and CRM platforms.",
    },
    {
      name: "Infrastructure & App Managed Services",
      description:
        "Ensure 24/7 operational resilience and peak performance. Simultaneously, lower overhead by optimizing costs and resource efficiency.",
    },
    {
      name: "Cybersecurity",
      description:
        "Safeguard digital assets, ensure strict compliance, and build trust with a proactive, zero-trust security framework.",
    },
    {
      name: "Software Engineering",
      description: "Deliver high-quality, custom applications at global speed with elite tech talent.",
    },
  ],
  stats: [
    { value: "32+", label: "Years Of Experience", note: "Part of CMC Corporation, founded 1993." },
    { value: "30+", label: "Countries", note: "Delivery presence across three regions." },
    { value: "300+", label: "Global Clients", note: "Bosch, Honda, AIA, IBM, LINE and more." },
    { value: "35+", label: "Business Partners", note: "AWS, Google Cloud, UiPath, Salesforce." },
    { value: "3000+", label: "Employees", note: "Across delivery centres worldwide." },
  ],
  offices: [
    {
      city: "Hanoi Office (Headquarter)",
      lat: 21.03,
      lon: 105.78,
      flag: "#da251d",
      big: true,
      address:
        "7 - 10F, CMC Tower, 11 Duy Tan Street, Cau Giay\nWard, Hanoi 100000, Vietnam",
    },
    {
      city: "Da Nang Office",
      lat: 16.07,
      lon: 108.22,
      flag: "#da251d",
      address:
        "G8 Golden Building, 65 Hai Phong Street,\nHai Chau Ward, Da Nang, Vietnam",
    },
    {
      city: "Ho Chi Minh City Office",
      lat: 10.75,
      lon: 106.73,
      flag: "#da251d",
      address:
        "CMC Creative Space, Street 19, Tan Thuan Export\nProcessing Zone, Tan Thuan Ward, Ho Chi Minh City, Vietnam",
    },
    {
      city: "Tokyo Office",
      lat: 35.62,
      lon: 139.74,
      flag: "#bc002d",
      address:
        "Sumitomo Fudosan Osaki Twin Bldg. East 7F, 5-1-18\nKitashinagawa, Shinagawa-ku, Tokyo 141-0001, Japan",
    },
    {
      city: "Osaka Office",
      lat: 34.7,
      lon: 135.5,
      flag: "#bc002d",
      big: true,
      address:
        "Midosuji Frontier Bldg. 14F, 1-13-22 Sonezakishinchi,\nKita-ku, Osaka 530-0002, Japan",
    },
    {
      city: "Singapore Office",
      lat: 1.28,
      lon: 103.85,
      flag: "#ed2939",
      address:
        "1 Finlayson Green, #15-02, One Finlayson Green,\nSingapore 049246",
    },
  ],
  caseStudies: [
    {
      title: "36% Higher Productivity & 40% Faster QA With Agentic AI For Intrusion Detection Software",
      date: "June 25, 2026",
      category: "Agentic AI Software Delivery",
    },
    {
      title: "15% Increase In Revenue With AI Energy Trading System",
      date: "June 20, 2026",
      category: "AI-Powered Trading & Forecasting",
    },
    {
      title: "30% Trips Return Reduced With Smart Field Service Software Using Azure Open AI",
      date: "June 2, 2026",
      category: "AI-Powered Field Operations",
    },
    {
      title: "Beyond a Vendor: Envato CTO on Why Trust and “Unbreakable Connectivity” Won the Deal",
      date: "March 2, 2026",
      category: "Software Engineering",
    },
  ],
  insights: [
    {
      title: "Vietnam-China AI and Digital Economy Dialogue 2026: A New Chapter for Bilateral Tech Cooperation",
      date: "August 28, 2026",
    },
    { title: "APEC Workshop: Putting AI And Digital Transformation Into Practice", date: "July 18, 2026" },
    { title: "CMC Global Signs A Cooperation MOU With New Zealand Partners", date: "July 4, 2026" },
    { title: "CMC Global x Salesforce: Accelerating Enterprise AI Adoption", date: "June 20, 2026" },
    { title: "Event Highlights: Building The Next Generation Of Engineers", date: "June 6, 2026" },
  ],
  awards: [
    { name: "Award 1" },
    { name: "Award 2" },
    { name: "Award 3" },
    { name: "Award 4" },
    { name: "Award 5" },
  ],
  certifications: [
    { name: "ISO 9001" },
    { name: "ISO 27001" },
    { name: "ITIL" },
    { name: "PCI-DSS" },
    { name: "AWS Advanced Tier Services" },
  ],
  partners: [
    { name: "ITIL" },
    { name: "AWS" },
    { name: "Google Cloud" },
    { name: "UiPath" },
    { name: "ISTQB" },
  ],
  testimonials: [
    {
      name: "Mr. Nick Benjamin",
      title: "CTO",
      company: "envato",
      quote:
        "CMC Global’s helped us in a number of different ways. One is that CMC Global’s actually delivered what they said they would do. And so for me, that’s foundational.",
      flankFrom: "#87e64c",
      flankTo: "#000000",
    },
    {
      name: "Mr. Kim Teak Joong",
      title: "CTO",
      company: "RSUPPORT",
      quote:
        "CMC Global’s greatest strength lies in its proactive approach to understanding and addressing customer needs.",
      flankFrom: "#ad0021",
      flankTo: "#47000d",
    },
    {
      name: "Mr. Le Minh",
      title: "Chief Product Officer",
      company: "northstar",
      quote:
        "Flexibility is definitely one of the character that we value most about CMC Global.",
      flankFrom: "#404040",
      flankTo: "#000000",
    },
  ],
  footerNav: [
    {
      label: "Services",
      links: [
        "Consulting & Strategy",
        "Data & AI",
        "Digital & Cloud",
        "Digital Engineering",
        "Enterprise Solutions",
        "Managed Services",
      ],
    },
    { label: "Industries", links: ["Banking, Finance & Insurance", "Healthcare", "Manufacturing"] },
    { label: "Insights", links: ["Perspectives", "Case Studies", "Blogs", "Client Speak"] },
    { label: "About", links: ["Our Organization", "Our Partners", "Our Leaders", "Newsroom", "Events"] },
  ],
};
