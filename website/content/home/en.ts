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
        linkUrl: "#cases",
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
      { section: "leaders", eyebrow: "Our People", title: "Leadership Team" },
      {
        section: "industry-solutions",
        eyebrow: "Solutions",
        title: "Our Solutions",
        body: "Designed to modernize banking operations, strengthen resilience, and accelerate digital innovation.",
      },
      {
        section: "industry-why",
        eyebrow: "Why CMCG",
        title: "Reasons to Partner with Us",
        body: "Built for Finance Reliability, Speed, and Compliance",
      },
      {
        section: "industry-stories",
        eyebrow: "Case Studies",
        title: "Success Stories",
        body: "Real products. Real challenges. Real results.",
      },
      {
        section: "industry-insights",
        eyebrow: "Insights",
        title: "Building Tomorrow Together",
        body: "For building secure, efficient, and future-ready financial services.",
        linkLabel: "Explore all Insights",
        linkUrl: "/insights",
      },
    ],
    industry_hero: [
      {
        breadcrumb: "Industries > Banking, Finance & Insurance",
        title: "Accelerate Financial Innovation with Compliance at the Core",
        subtitle: "Turning financial complexity into competitive advantage through intelligent technology.",
        ctaLabel: "Let's talk!",
        ctaUrl: "#contact",
        stats: [
          {
            value: "15-20%",
            label: "higher sales-conversion rates among banks thanks to experience-led banking transformations",
          },
          {
            value: "25-30%",
            label: "reduction in claims expenses after deploying digital claims transformations",
          },
        ],
      },
    ],
    industry_solution: [
      {
        group: "Core Banking",
        tabLabel: "Thought Machine Solution",
        title: "Core Banking Solutions",
        columnA:
          "Smart contract customization\nIntegration with channels & fintech APIs\nCloud deployment & DevOps automation",
        columnB: "Data migration & automated testing\n24/7 support & maintenance",
      },
      {
        group: "Core Banking",
        tabLabel: "Temenos Solution",
        title: "Temenos Solution",
        columnA:
          "Module customization & integration resources\nBusiness analyst with banking domain expertise\nParameterization, scripting, and interface development",
        columnB:
          "Testing, defect resolution & technical documentation\nFlexible resources scaling across project phases",
      },
      {
        group: "Digital Banking",
        tabLabel: "Backbase Solution",
        title: "Digital Banking Solutions",
        columnA:
          "Backbase implementation & configuration\nModule customization & branding\nAPI integration with core banking & fintech services",
        columnB: "Performance optimization & upgrades\nOngoing support & enhancement",
      },
      {
        group: "Digital Banking",
        tabLabel: "Bespoke Solution",
        title: "Digital Banking Solutions",
        columnA:
          "End-to-end solution design & architecture\nCustom UI/UX design and development\nAPI design & integration with any backend systems",
        columnB: "Cloud-native or on-prem deployment\nLong-term maintenance & continuous improvement",
      },
      {
        group: "Open Banking",
        tabLabel: "Open banking platform (WSO2, Oracle, Temenos)",
        title: "Open Banking Solutions",
        columnA:
          "Platform implementation & configuration\nAPI customization & branding\nIntegration with core banking & fintech",
        columnB: "Compliance validation\nOngoing support & version upgrades",
      },
      {
        group: "Open Banking",
        tabLabel: "Bespoke Solution",
        title: "Open Banking Solutions",
        columnA:
          "End-to-end API architecture & development\nCustom API gateway, security and monitoring\nFull integration strategy with legacy & modern systems",
        columnB: "Regulatory compliance design and testing\nContinuous improvement & new API product creation",
      },
    ],
    industry_reason: [
      { title: "Financial Services DNA" },
      { title: "Enterprise-grade Security" },
      { title: "Modern Banking Ecosystem Expertise" },
    ],
    industry_story: [
      {
        tag: "BFSI",
        title: "HD Bank\nBespoke Digital Banking Solution",
        body: "One of leading Vietnamese banks offering a wide range of financial services, including retail, corporate banking and digital banking solutions",
        linkLabel: "Read the case study →",
        linkUrl: "/insights",
        heading: "Bespoke Digital Banking Solution",
        metrics: [
          { value: "316", label: "MM" },
          { value: "8", label: "Months" },
          { value: "100%", label: "Milestone Completed" },
          { value: "100%", label: "Customer Satisfied" },
        ],
      },
      {
        tag: "Banking & Finance",
        title: "An Entrepreneurial Business Bank",
        body: "A well-established bank offers a range of financial products and services include business and personal banking, equipment loans, working capital, acquisition financing, small business administration lending, cash management, and so on.",
        linkLabel: "Read the case study →",
        linkUrl: "/insights",
      },
    ],
    about_hero: [
      {
        breadcrumb: "About Us",
        title: "Transforming Ideas into Global Impact, Together",
        subtitle:
          "We unite passionate experts with advanced technology to scale your most ambitious digital vision",
        ctaLabel: "Meet the people behind our promise",
        ctaUrl: "#leaders",
        stats: [
          { value: "33+", label: "Years of Engineering Excellence" },
          { value: "3000+", label: "Engineering Experts" },
          { value: "10+", label: "Global Offices" },
        ],
      },
    ],
    about_intro: [
      {
        eyebrow: "Meet CMC Global",
        title: "Meet CMC Global",
        body: "At CMC Global, we believe true digital transformation is not about chasing the next big trend. It’s about mastering it to redefine what’s possible, and making technology work seamlessly for the business. Built on the deep technical foundation of CMC Corporation, we exist to turn complex technology into your clearest competitive advantage.\n\nAs global business enters the AI-first landscape, we architect intelligent, future-ready organizations from the ground up. By bridging technical mastery with global scale, we stand as the reliable partner behind your boldest ambitions, helping over 300 global leaders modernize with certainty and unlock steady growth.",
        statsTitle: "Our Growth in Numbers",
      },
    ],
    about_statement: [
      {
        variant: "vision",
        eyebrow: "Our Vision",
        title: "Establishing a global presence\n& providing world-class services",
      },
      {
        variant: "mission",
        eyebrow: "Our Mission",
        body: "Our mission is simple: to make complex technology work for people and businesses, everywhere.\n\nWe empower organizations to grow with confidence through practical innovation, while building a culture where talent thrives, learns, and creates meaningful impact every day.",
      },
    ],
    leader: [
      { name: "Mr. Ho Thanh Tung", title: "President of CMC Global" },
      { name: "Mr. Dang Ngoc Bao", title: "Chief Executive Officer of CMC Global" },
      { name: "Mr. Nguyen Viet Bach", title: "Deputy CEO / Chief Delivery Officer of CMC Global" },
      { name: "Ms. Hoang Thi Thanh Hoa", title: "Chief Finance Officer of CMC Global" },
      { name: "Mr. Nguyen Cao Cuong", title: "Chief Sales Officer of CMC Global" },
    ],
    feature_band: [
      {
        eyebrow: "Your Extended Tech Workforce",
        title: "Scale your tech capabilities with precision and agility",
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
    hero_slide: [
      {
        title: "Riding The Next Wave of AI Transformation",
        subtitle: "Reinvent Your Base - Scale Without Limits - Engineer Your Tomorrow",
        ctaLabel: "Contact Us",
        ctaUrl: "#contact",
        layout: "center",
        ctaStyle: "white",
        backgroundColor: "#000812",
        backgroundColorEnd: "#00152d",
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
