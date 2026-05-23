/**
 * Generates a Postman Collection v2.1 from api-docs.json (OpenAPI 3.0)
 * Organizes requests by tag into folders, preserves auth/variable settings.
 */

const fs = require('fs');

const spec = JSON.parse(fs.readFileSync('api-docs.json', 'utf8'));

// ── Helpers ────────────────────────────────────────────────────────────────

function tagToFolderName(tag) {
  const map = {
    'User Account':                                   'User Account',
    'Wallet Management':                              'Wallet Management',
    'Public Market Data':                             'Public Market Data',
    'Futures Trading':                                'Futures Trading',
    'Futures Trade Log':                              'Futures Trade Log',
    'Margin Trading':                                 'Margin Trading',
    'Lending':                                        'Lending',
    'Order Management':                               'Order Management',
    'API Monitoring':                                 'API Monitoring',
    'hello-controller':                               'Health Check',
    'web-socket-subscription-controller':             'WebSocket - Subscriptions',
    'web-socket-controller':                          'WebSocket - Management',
    'web-socket-market-controller':                   'WebSocket - Market Control',
    'web-socket-data-controller':                     'WebSocket - Data Management',
    'web-socket-spot-current-price-controller':       'WebSocket - Spot Current Prices',
    'web-socket-spot-price-change-controller':        'WebSocket - Spot Price Change',
    'web-socket-spot-price-stats-controller':         'WebSocket - Spot Price Stats',
    'web-socket-spot-new-trade-controller':           'WebSocket - Spot New Trades',
    'web-socket-spot-trade-update-controller':        'WebSocket - Spot Trade Updates',
    'web-socket-spot-order-update-controller':        'WebSocket - Spot Order Updates',
    'web-socket-spot-depth-snapshot-controller':      'WebSocket - Spot Depth Snapshots',
    'web-socket-spot-depth-update-controller':        'WebSocket - Spot Depth Updates',
    'web-socket-spot-candlestick-controller':         'WebSocket - Spot Candlesticks',
    'web-socket-balance-update-controller':           'WebSocket - Balance Updates',
    'web-socket-spot-balance-controller':             'WebSocket - Spot Balance',
    'web-socket-futures-current-prices-controller':   'WebSocket - Futures Current Prices',
    'web-socket-futures-instrument-price-controller': 'WebSocket - Futures Instrument Prices',
    'web-socket-futures-new-trade-controller':        'WebSocket - Futures New Trades',
    'web-socket-futures-order-update-controller':     'WebSocket - Futures Order Updates',
    'web-socket-futures-position-update-controller':  'WebSocket - Futures Position Updates',
    'web-socket-futures-orderbook-controller':        'WebSocket - Futures Orderbook',
    'web-socket-futures-candlestick-controller':      'WebSocket - Futures Candlesticks',
  };
  return map[tag] || tag;
}

// Resolve a $ref inside the spec
function resolveRef(ref) {
  if (!ref) return null;
  const parts = ref.replace('#/', '').split('/');
  let node = spec;
  for (const p of parts) node = node[p];
  return node;
}

// Resolve schema (following $ref if needed)
function resolveSchema(schema) {
  if (!schema) return null;
  if (schema['$ref']) return resolveRef(schema['$ref']);
  return schema;
}

// Generate an example body object from a JSON schema
function schemaToExample(schema, depth = 0) {
  if (!schema || depth > 4) return null;
  const s = resolveSchema(schema);
  if (!s) return null;
  if (s.example !== undefined) return s.example;
  if (s.type === 'object' || s.properties) {
    const obj = {};
    const props = s.properties || {};
    for (const [k, v] of Object.entries(props)) {
      obj[k] = schemaToExample(v, depth + 1);
    }
    return obj;
  }
  if (s.type === 'array') {
    const item = schemaToExample(s.items, depth + 1);
    return item !== null ? [item] : [];
  }
  if (s.type === 'string') return s.example || s.enum?.[0] || '';
  if (s.type === 'integer' || s.type === 'number') return s.example ?? 0;
  if (s.type === 'boolean') return false;
  return null;
}

// Convert a URL path like /api/futures/{id} → raw+host+path postman format
function buildUrl(pathStr, parameters) {
  const host = '{{base_url}}';
  // Replace {param} with :param for Postman
  const postmanPath = pathStr.replace(/\{([^}]+)\}/g, ':$1');
  const segments = postmanPath.replace(/^\//, '').split('/');

  const queryParams = (parameters || [])
    .filter(p => (p['$ref'] ? resolveRef(p['$ref']) : p).in === 'query')
    .map(p => {
      const rp = p['$ref'] ? resolveRef(p['$ref']) : p;
      return {
        key: rp.name,
        value: rp.example !== undefined ? String(rp.example) : '',
        description: rp.description || '',
        disabled: !rp.required
      };
    });

  const pathVars = (parameters || [])
    .filter(p => (p['$ref'] ? resolveRef(p['$ref']) : p).in === 'path')
    .map(p => {
      const rp = p['$ref'] ? resolveRef(p['$ref']) : p;
      return { key: rp.name, value: rp.example !== undefined ? String(rp.example) : '' };
    });

  const urlObj = {
    raw: host + pathStr + (queryParams.filter(q => !q.disabled).length
      ? '?' + queryParams.filter(q => !q.disabled).map(q => `${q.key}=${q.value}`).join('&')
      : ''),
    host: [host],
    path: segments
  };
  if (queryParams.length) urlObj.query = queryParams;
  if (pathVars.length) urlObj.variable = pathVars;
  return urlObj;
}

// Determine if an operation requires auth (has security or server-level security)
function requiresAuth(operation) {
  if (operation.security !== undefined) {
    return operation.security.length > 0;
  }
  // Use global security if defined
  return (spec.security || []).length > 0;
}

// Build request body from operation
function buildBody(operation) {
  const rb = operation.requestBody;
  if (!rb) return undefined;
  const resolved = rb['$ref'] ? resolveRef(rb['$ref']) : rb;
  const content = resolved.content || {};
  const jsonContent = content['application/json'];
  if (!jsonContent) return undefined;
  const schema = resolveSchema(jsonContent.schema);
  const example = schemaToExample(schema);
  return {
    mode: 'raw',
    raw: JSON.stringify(example, null, 2),
    options: { raw: { language: 'json' } }
  };
}

// Build headers for a request
function buildHeaders(operation) {
  const headers = [];
  const rb = operation.requestBody;
  if (rb) {
    const resolved = rb['$ref'] ? resolveRef(rb['$ref']) : rb;
    if (resolved.content && resolved.content['application/json']) {
      headers.push({ key: 'Content-Type', value: 'application/json' });
    }
  }
  return headers;
}

// Make a human-readable request name from method + path
function requestName(method, pathStr, operation) {
  if (operation.summary) return operation.summary;
  // Strip leading /api/ prefix and build readable name
  const segments = pathStr.replace(/^\/api\//, '').split('/').filter(Boolean);
  // Convert each segment: {id} → by ID, kebab → Title Case
  const parts = segments.map(seg => {
    if (seg.startsWith('{') && seg.endsWith('}')) {
      return 'by ' + seg.slice(1, -1).replace(/-/g, ' ');
    }
    return seg.replace(/-/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
  });
  const verb = {
    GET: 'Get', POST: 'Post', PUT: 'Update', PATCH: 'Patch', DELETE: 'Delete'
  }[method.toUpperCase()] || method.toUpperCase();
  return verb + ' ' + parts.join(' ');
}

// ── Build collection ────────────────────────────────────────────────────────

const folders = {}; // folderName → [request items]

for (const [pathStr, pathObj] of Object.entries(spec.paths)) {
  for (const [method, operation] of Object.entries(pathObj)) {
    if (['get','post','put','patch','delete','head','options'].indexOf(method) === -1) continue;

    const tags = operation.tags || ['Other'];
    const tag = tags[0];
    const folderName = tagToFolderName(tag);

    if (!folders[folderName]) folders[folderName] = [];

    const allParams = [
      ...(pathObj.parameters || []),
      ...(operation.parameters || [])
    ];

    const request = {
      name: requestName(method, pathStr, operation),
      request: {
        method: method.toUpperCase(),
        header: buildHeaders(operation),
        url: buildUrl(pathStr, allParams),
        description: operation.description || operation.summary || ''
      }
    };

    // Add body for POST/PUT/PATCH
    const body = buildBody(operation);
    if (body) request.request.body = body;

    folders[folderName].push(request);
  }
}

// Sort folder order: named folders first, then WebSocket folders alphabetically
const FOLDER_ORDER = [
  'Health Check',
  'User Account',
  'Wallet Management',
  'Public Market Data',
  'Order Management',
  'Futures Trading',
  'Futures Trade Log',
  'Margin Trading',
  'Lending',
  'API Monitoring',
];

const sortedFolderNames = [
  ...FOLDER_ORDER.filter(f => folders[f]),
  ...Object.keys(folders)
      .filter(f => !FOLDER_ORDER.includes(f))
      .sort()
];

const collectionItems = sortedFolderNames.map(name => ({
  name,
  item: folders[name]
}));

const collection = {
  info: {
    name: 'CoinDCX Spring Client API',
    description: spec.info.description || 'REST API collection for CoinDCX Spring Boot client',
    schema: 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',
    _exporter_id: 'coindcx-spring-client'
  },
  auth: {
    type: 'apikey',
    apikey: [
      { key: 'value', value: '{{api_key}}', type: 'string' },
      { key: 'key', value: 'X-AUTH-APIKEY', type: 'string' }
    ]
  },
  variable: [
    { key: 'base_url', value: 'http://localhost:8080', type: 'string' },
    { key: 'api_key', value: 'your_api_key_here', type: 'string' },
    { key: 'api_secret', value: 'your_api_secret_here', type: 'string' }
  ],
  item: collectionItems
};

fs.writeFileSync(
  'CoinDCX-Spring-Client.postman_collection.json',
  JSON.stringify(collection, null, 2)
);

// Summary
let total = 0;
collectionItems.forEach(f => {
  console.log(`${f.item.length.toString().padStart(4)}  ${f.name}`);
  total += f.item.length;
});
console.log(`\nTotal requests: ${total}`);
console.log('Written to CoinDCX-Spring-Client.postman_collection.json');
