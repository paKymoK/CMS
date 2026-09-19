import { geoEquirectangular, geoOrthographic, geoDistance, geoPath } from "d3-geo";
import type { GeoSphere } from "d3-geo";
import { feature } from "topojson-client";
import type { Topology, GeometryCollection } from "topojson-specification";

export type GlobeDot = { lon: number; lat: number };

const RASTER_WIDTH = 720;
const RASTER_HEIGHT = 360;

/**
 * Rasterises the world landmass to an offscreen equirectangular canvas once,
 * then samples it on a latitude grid (longitude step divided by cos(lat) so
 * dot density stays even toward the poles), keeping points that land on
 * land (alpha > threshold). Mirrors the design handoff's documented
 * technique — never hand-draw coastlines. Browser-only (uses <canvas>).
 */
export function buildGlobeDots(
  topology: Topology,
  { latStepDeg = 1.9, alphaThreshold = 120 }: { latStepDeg?: number; alphaThreshold?: number } = {},
): GlobeDot[] {
  const canvas = document.createElement("canvas");
  canvas.width = RASTER_WIDTH;
  canvas.height = RASTER_HEIGHT;
  const ctx = canvas.getContext("2d");
  if (!ctx) return [];

  const sphere: GeoSphere = { type: "Sphere" };
  const projection = geoEquirectangular().fitSize([RASTER_WIDTH, RASTER_HEIGHT], sphere);
  const path = geoPath(projection, ctx);
  const land = feature(topology, topology.objects.land as GeometryCollection);

  ctx.fillStyle = "#000";
  ctx.beginPath();
  path(land);
  ctx.fill();

  const { data } = ctx.getImageData(0, 0, RASTER_WIDTH, RASTER_HEIGHT);
  const dots: GlobeDot[] = [];

  for (let lat = -90; lat <= 90; lat += latStepDeg) {
    const cosLat = Math.max(Math.cos((lat * Math.PI) / 180), 0.02);
    const lonStep = latStepDeg / cosLat;
    for (let lon = -180; lon < 180; lon += lonStep) {
      const projected = projection([lon, lat]);
      if (!projected) continue;
      const x = Math.round(projected[0]);
      const y = Math.round(projected[1]);
      if (x < 0 || x >= RASTER_WIDTH || y < 0 || y >= RASTER_HEIGHT) continue;
      const alpha = data[(y * RASTER_WIDTH + x) * 4 + 3];
      if (alpha > alphaThreshold) dots.push({ lon, lat });
    }
  }

  return dots;
}

export type ProjectedPoint = {
  x: number;
  y: number;
  /** 0 = view centre, 1 = horizon/limb, occluded points are already
   * filtered out. Use to fade points approaching the limb. */
  depth: number;
};

/**
 * A rotatable orthographic view over the sphere: `project` returns the
 * screen-space position + depth (angular distance from view centre,
 * normalised 0..1) for a single [lon, lat] point, or null if it's on the
 * far side of the globe (occluded). Shared by the dot field and the office
 * markers so both use identical projection math.
 */
export function createOrthographicView({
  size,
  rotationLon,
  tiltDeg = 14,
}: {
  size: number;
  rotationLon: number;
  tiltDeg?: number;
}) {
  const projection = geoOrthographic()
    .clipAngle(90)
    .translate([size / 2, size / 2])
    .scale(size / 2)
    .rotate([rotationLon, -tiltDeg]);

  // The point currently facing the viewer — inverting the exact centre
  // pixel is the simplest correct way to get it regardless of the rotate
  // convention's sign/order.
  const center = (projection.invert?.([size / 2, size / 2]) ?? [0, 0]) as [number, number];

  function project(lon: number, lat: number): ProjectedPoint | null {
    const distRad = geoDistance([lon, lat], center);
    const depth = distRad / (Math.PI / 2);
    if (depth > 1) return null;
    const p = projection([lon, lat]);
    if (!p) return null;
    return { x: p[0], y: p[1], depth };
  }

  return { projection, project };
}

export function projectDots(
  dots: GlobeDot[],
  view: ReturnType<typeof createOrthographicView>,
): (GlobeDot & ProjectedPoint)[] {
  const out: (GlobeDot & ProjectedPoint)[] = [];
  for (const dot of dots) {
    const p = view.project(dot.lon, dot.lat);
    if (p) out.push({ ...dot, ...p });
  }
  return out;
}
