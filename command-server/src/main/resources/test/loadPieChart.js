const color = d3.scaleOrdinal(d3.schemeDark2);
d3.selection.prototype.appendHTML =
    d3.selection.prototype.appendHTML = function(HTMLString) {
        return this.select(function() {
            return this.appendChild(document.importNode(new DOMParser().parseFromString(HTMLString, 'text/html').body.childNodes[0], true));
        });
    };

d3.selection.prototype.appendSVG =
    d3.selection.prototype.appendSVG = function(SVGString) {
        return this.select(function() {
            return this.appendChild(document.importNode(new DOMParser()
            .parseFromString('<svg xmlns="http://www.w3.org/2000/svg">' + SVGString + '</svg>', 'application/xml').documentElement.firstChild, true));
        });
    };
//function loadPieChart(dataset) {
//  var svg = d3.select("#piechart");
//  var height = 500;
//  var width = 500;
//  var textLabelSuffix = "%";
//
//  showPieChart(dataset, svg, height, width, textLabelSuffix);
//}
//
//function showPieChart(dataset, svg, height, width,
//  textLabelSuffix)
//{
//  var outerRadius = width / 2;
//  var innerRadius = 0;
//
//  // set height/width to match the SVG element
//  svg.attr("height", height).attr("width", width);
//
//  // create a new pie layout
//  var pie = d3.pie();
//
//  // initialize arcs/wedges to span
//  // the radius of the circle
//  var arc = d3.arc()
//               .innerRadius(innerRadius)
//               .outerRadius(outerRadius);
//
//  // create groups
//  var arcs = svg.selectAll("g.arc")
//                // bind dataset to pie layout
//                .data(pie(dataset))
//                // create groups
//                .enter()
//                // append groups
//                .append("g")
//                // create arcs
//                .attr("class", "arc")
//                // position each arc in the pie layout
//                .attr("transform", "translate(" +
//                 outerRadius + "," +
//                 outerRadius + ")");
//
//
//  // initialize color scale - refer to
//  // <a href="https://github.com/mbostock/d3/wiki/Ordinal-Scales" target="_blank">https://github.com/mbostock/d3/wiki/Ordinal-Scales</a>
//  var color = d3.scaleOrdinal(d3.schemeCategory10);
//
//  arcs.append("path")
//      .attr("fill", function(d,i) { return color(i); })
//      .attr("d", arc);
//
//  arcs.append("text")
//      .attr("transform", function(d) {
//          return "translate(" + arc.centroid(d) + ")";
//       })
//      .attr("text-anchor", "middle")
//      .text(function(d) { return d.value +
//         textLabelSuffix; });
//}
const levelsData = [
                 [{id: 'Chaos'}],
                 [
                   {id: 'Gaea', parents: ['Chaos']},
                   {id: 'Uranus'}
                 ],
                 [
                   {id: 'Oceanus', parents: ['Gaea', 'Uranus']},
                   {id: 'Thethys', parents: ['Gaea', 'Uranus']},
                   {id: 'Pontus'},
                   {id: 'Rhea', parents: ['Gaea', 'Uranus']},
                   {id: 'Cronus', parents: ['Gaea', 'Uranus']},
                   {id: 'Coeus', parents: ['Gaea', 'Uranus']},
                   {id: 'Phoebe', parents: ['Gaea', 'Uranus']},
                   {id: 'Crius', parents: ['Gaea', 'Uranus']},
                   {id: 'Hyperion', parents: ['Gaea', 'Uranus']},
                   {id: 'Iapetus', parents: ['Gaea', 'Uranus']},
                   {id: 'Thea', parents: ['Gaea', 'Uranus']},
                   {id: 'Themis', parents: ['Gaea', 'Uranus']},
                   {id: 'Mnemosyne', parents: ['Gaea', 'Uranus']}
                 ],
                 [
                   {id: 'Doris', parents: ['Oceanus', 'Thethys']},
                   {id: 'Neures', parents: ['Pontus', 'Gaea']},
                   {id: 'Dionne'},
                   {id: 'Demeter', parents: ['Rhea', 'Cronus']},
                   {id: 'Hades', parents: ['Rhea', 'Cronus']},
                   {id: 'Hera', parents: ['Rhea', 'Cronus']},
                   {id: 'Alcmene'},
                   {id: 'Zeus', parents: ['Rhea', 'Cronus']},
                   {id: 'Eris'},
                   {id: 'Leto', parents: ['Coeus', 'Phoebe']},
                   {id: 'Amphitrite'},
                   {id: 'Medusa'},
                   {id: 'Poseidon', parents: ['Rhea', 'Cronus']},
                   {id: 'Hestia', parents: ['Rhea', 'Cronus']}
                 ],
                 [
                   {id: 'Thetis', parents: ['Doris', 'Neures']},
                   {id: 'Peleus'},
                   {id: 'Anchises'},
                   {id: 'Adonis'},
                   {id: 'Aphrodite', parents: ['Zeus', 'Dionne']},
                   {id: 'Persephone', parents: ['Zeus', 'Demeter']},
                   {id: 'Ares', parents: ['Zeus', 'Hera']},
                   {id: 'Hephaestus', parents: ['Zeus', 'Hera']},
                   {id: 'Hebe', parents: ['Zeus', 'Hera']},
                   {id: 'Hercules', parents: ['Zeus', 'Alcmene']},
                   {id: 'Megara'},
                   {id: 'Deianira'},
                   {id: 'Eileithya', parents: ['Zeus', 'Hera']},
                   {id: 'Ate', parents: ['Zeus', 'Eris']},
                   {id: 'Leda'},
                   {id: 'Athena', parents: ['Zeus']},
                   {id: 'Apollo', parents: ['Zeus', 'Leto']},
                   {id: 'Artemis', parents: ['Zeus', 'Leto']},
                   {id: 'Triton', parents: ['Poseidon', 'Amphitrite']},
                   {id: 'Pegasus', parents: ['Poseidon', 'Medusa']},
                   {id: 'Orion', parents: ['Poseidon']},
                   {id: 'Polyphemus', parents: ['Poseidon']}
                 ],
                 [
                   {id: 'Deidamia'},
                   {id: 'Achilles', parents: ['Peleus', 'Thetis']},
                   {id: 'Creusa'},
                   {id: 'Aeneas', parents: ['Anchises', 'Aphrodite']},
                   {id: 'Lavinia'},
                   {id: 'Eros', parents: ['Hephaestus', 'Aphrodite']},
                   {id: 'Helen', parents: ['Leda', 'Zeus']},
                   {id: 'Menelaus'},
                   {id: 'Polydueces', parents: ['Leda', 'Zeus']}
                 ],
                 [
                   {id: 'Andromache'},
                   {id: 'Neoptolemus', parents: ['Deidamia', 'Achilles']},
                   {id: 'Aeneas(2)', parents: ['Creusa', 'Aeneas']},
                   {id: 'Pompilius', parents: ['Creusa', 'Aeneas']},
                   {id: 'Iulus', parents: ['Lavinia', 'Aeneas']},
                   {id: 'Hermione', parents: ['Helen', 'Menelaus']}
                 ]
               ]

const loadData = (levels)=> {

               // precompute level depth
               levels.forEach((l,i) => l.forEach(n => n.level = i))

               var nodes = levels.reduce( ((a,x) => a.concat(x)), [] )
               var nodes_index = {}
               nodes.forEach(d => nodes_index[d.id] = d)

               // objectification
               nodes.forEach(d => {
                 d.parents = (d.parents === undefined ? [] : d.parents).map(p => nodes_index[p])
               })

               // precompute bundles
               levels.forEach((l, i) => {
                 var index = {}
                 l.forEach(n => {
                   if(n.parents.length == 0) {
                     return
                   }
                    console.log("parents:::")
                    console.log(n.parents)
                   var id = n.parents.map(d => d.id).sort().join('--')
                   if (id in index) {
                     index[id].parents = index[id].parents.concat(n.parents)
                   }
                   else {
                     index[id] = {id: id, parents: n.parents.slice(), level: i}
                   }
                   n.bundle = index[id]
                 })
                 l.bundles = Object.keys(index).map(k => index[k])
                 l.bundles.forEach((b, i) => b.i = i)
               })

               var links = []
               nodes.forEach(d => {
                 d.parents.forEach(p => links.push({source: d, bundle: d.bundle, target: p}))
               })

               var bundles = levels.reduce( ((a,x) => a.concat(x.bundles)), [] )

               // reverse pointer from parent to bundles
               bundles.forEach(b => b.parents.forEach(p => {
                 if(p.bundles_index === undefined) {
                   p.bundles_index = {}
                 }
                 if(!(b.id in p.bundles_index)) {
                   p.bundles_index[b.id] = []
                 }
                 p.bundles_index[b.id].push(b)
               }))

               nodes.forEach(n => {
                 if(n.bundles_index !== undefined) {
                   n.bundles = Object.keys(n.bundles_index).map(k => n.bundles_index[k])
                 }
                 else {
                   n.bundles_index = {}
                   n.bundles = []
                 }
                 n.bundles.forEach((b, i) => b.i = i)
               })

               links.forEach(l => {
                 if(l.bundle.links === undefined) {
                   l.bundle.links = []
                 }
                 l.bundle.links.push(l)
               })

               // layout
               const padding = 8
               const node_height = 22
               const node_width = 70
               const bundle_width = 14
               const level_y_padding = 16
               const metro_d = 4
               const c = 16
               const min_family_height = 16

               nodes.forEach(n => n.height = (Math.max(1, n.bundles.length)-1)*metro_d)

               var x_offset = padding
               var y_offset = padding
               levels.forEach(l => {
                 x_offset += l.bundles.length*bundle_width
                 y_offset += level_y_padding
                 l.forEach((n, i) => {
                   n.x = n.level*node_width + x_offset
                   n.y = node_height + y_offset + n.height/2

                   y_offset += node_height + n.height
                 })
               })

               var i = 0
               levels.forEach(l => {
                 l.bundles.forEach(b => {
                   b.x = b.parents[0].x + node_width + (l.bundles.length-1-b.i)*bundle_width
                   b.y = i*node_height
                 })
                 i += l.length
               })

               links.forEach(l => {
                 l.xt = l.target.x
                 l.yt = l.target.y + l.target.bundles_index[l.bundle.id].i*metro_d - l.target.bundles.length*metro_d/2 + metro_d/2
                 l.xb = l.bundle.x
                 l.xs = l.source.x
                 l.ys = l.source.y
               })

               // compress vertical space
               var y_negative_offset = 0
               levels.forEach(l => {
                 y_negative_offset += -min_family_height + d3.min(l.bundles, b => d3.min(b.links, link => (link.ys-c)-(link.yt+c))) || 0
                 l.forEach(n => n.y -= y_negative_offset)
               })

               // very ugly, I know
               links.forEach(l => {
                 l.yt = l.target.y + l.target.bundles_index[l.bundle.id].i*metro_d - l.target.bundles.length*metro_d/2 + metro_d/2
                 l.ys = l.source.y
                 l.c1 = l.source.level-l.target.level > 1 ? node_width+c : c
                 l.c2 = c
               })

               var layout = {
                 height: d3.max(nodes, n => n.y) + node_height/2 + 2*padding,
                 node_height,
                 node_width,
                 bundle_width,
                 level_y_padding,
                 metro_d
               }

               return {levels, nodes, nodes_index, links, bundles, layout}
             };
//require.config({paths: {d3: "http://d3js.org/d3.v3.min"}});

//require(["d3"], function(d3) {
//    console.log("d3: ")
//    console.log(d3.version);
//});
//const d3 = require('d3-scale', 'd3-scale-chromatic', 'd3-array')


//var newElement = document.createElementNS("http://www.w3.org/2000/svg", 'path');
const loadPieChart = (commandsData)=>{
    const width = 1000
    const height = 1000

//    console.log(commandsDataAsString)
//    const commandsData = JSON.parse(commandsDataAsString)
    console.log(commandsData);
    const data = loadData(commandsData.levels);
//    const data = loadData(levelsData)
//    const x = d3.scaleLinear()
//            //.domain(d3.extent(data, d => d[0]))
//            .range([30, width - 10])
//            .nice()
//    const y = d3.scaleLinear()
//          //.domain(d3.extent(data, d => d[1]))
//          .range([height - 20, 10])
//          .nice()
    const svg = d3.select("body")
                  .select("svg")
    svg.selectAll("text").remove();
    svg.selectAll("path").remove();
    svg.selectAll("line").remove();
//                    .attr("width", "100vh")
//                    .attr("height", "100vh")
//   const gx = svg.append("g");
//   const gy = svg.append("g");


    //const  svg = document.getElementsByTagName('svg')[0]; //Get svg element
    data.bundles.map(b => {
  let d = b.links.map(l => `
  M${ l.xt } ${ l.yt }
  L${ l.xb-l.c1 } ${ l.yt }
  A${ l.c1 } ${ l.c1 } 90 0 1 ${ l.xb } ${ l.yt+l.c1 }
  L${ l.xb } ${ l.ys-l.c2 }
  A${ l.c2 } ${ l.c2 } 90 0 0 ${ l.xb+l.c2 } ${ l.ys }
  L${ l.xs } ${ l.ys }`
  ).join("");
  return [
  `<path class="link" d="${ d }" stroke="white" stroke-width="5"/>`,
  `<path class="link" d="${ d }" stroke="${ color(b.id) }" stroke-width="2"/>`
  ]
  }).forEach(paths=> paths.forEach(path => svg.appendSVG(path)));

  data.nodes.map(n => [
    `<line class="node" stroke="black" stroke-width="8" x1="${ n.x }" y1="${ n.y-n.height/2 }" x2="${ n.x }" y2="${ n.y+n.height/2 }"/>`,
    `<line class="node" stroke="white" stroke-width="4" x1="${ n.x }" y1="${ n.y-n.height/2 }" x2="${ n.x }" y2="${ n.y+n.height/2 }"/>`,
    `<text x="${ n.x+4}" y="${ n.y-n.height/2-4 }" stroke="white" stroke-width="2">${ n.id }</text>`,
    `<text x="${ n.x+4}" y="${ n.y-n.height/2-4 }">${ n.id }</text>`
    ]).forEach(lineAndTexts => lineAndTexts.forEach(lineAndText => svg.appendSVG(lineAndText)));
//
//      // z holds a copy of the previous transform, so we can track its changes
//      let z = d3.zoomIdentity;
//
//      // set up the ancillary zooms and an accessor for their transforms
//      const zoomX = d3.zoom().scaleExtent([0.1, 10]);
//      const zoomY = d3.zoom().scaleExtent([0.2, 5]);
//      const tx = () => d3.zoomTransform(gx.node());
//      const ty = () => d3.zoomTransform(gy.node());
//      gx.call(zoomX).attr("pointer-events", "none");
//      gy.call(zoomY).attr("pointer-events", "none");
//
//      // active zooming
      const zoom = d3.zoom().on("zoom", function(e) {
//        const t = e.transform;
                //        const k = t.k / z.k;
                //        const point = e.sourceEvent ? d3.pointer(e) : [width / 2, height / 2];
                //
                //        // is it on an axis? is the shift key pressed?
                //        const doX = point[0] > x.range()[0];
                //        const doY = point[1] < y.range()[0];
                //        const shift = e.sourceEvent && e.sourceEvent.shiftKey;
                //
                //        if (k === 1) {
                //          // pure translation?
                //          doX && gx.call(zoomX.translateBy, (t.x - z.x) / tx().k, 0);
                //          doY && gy.call(zoomY.translateBy, 0, (t.y - z.y) / ty().k);
                //        } else {
                //          // if not, we're zooming on a fixed point
                //          doX && gx.call(zoomX.scaleBy, shift ? 1 / k : k, point);
                //          doY && gy.call(zoomY.scaleBy, k, point);
                //        }
                //
                //        z = t;

        //redraw();
      });

      return svg
        .call(zoom)
        .call(zoom.transform, d3.zoomIdentity.scale(0.8))
        .node();
}

//function redraw() {
//    const xr = tx().rescaleX(x);
//    const yr = ty().rescaleY(y);
//
//    gx.call(xAxis, xr);
//    gy.call(yAxis, yr);
//
//    dots
//      .attr("cx", d => xr(d[0]))
//      .attr("cy", d => yr(d[1]))
//      .attr("rx", 6 * Math.sqrt(tx().k))
//      .attr("ry", 6 * Math.sqrt(ty().k));
//
//    vo.attr(
//      "d",
//      d3.Delaunay.from(data.map(d => [xr(d[0]), yr(d[1])]))
//        .voronoi([35, 0, width, height - 25])
//        .render()
//    )
//      .attr("fill", "none")
//      .attr("stroke", "#ccc")
//      .attr("stroke-width", 0.5);
//  }

//console.log("data: ")
//console.log(data);
