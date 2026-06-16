     1|package space.jtcao.visepanda.ui.map
     2|
     3|import android.graphics.drawable.Drawable
     4|import androidx.compose.foundation.layout.Box
     5|import androidx.compose.foundation.layout.Column
     6|import androidx.compose.foundation.layout.fillMaxSize
     7|import androidx.compose.foundation.layout.padding
     8|import androidx.compose.foundation.shape.RoundedCornerShape
     9|import androidx.compose.material3.Card
    10|import androidx.compose.material3.CardDefaults
    11|import androidx.compose.material3.MaterialTheme
    12|import androidx.compose.material3.Text
    13|import androidx.compose.runtime.Composable
    14|import androidx.compose.runtime.collectAsState
    15|import androidx.compose.runtime.getValue
    16|import androidx.compose.runtime.mutableStateOf
    17|import androidx.compose.runtime.remember
    18|import androidx.compose.runtime.setValue
    19|import androidx.compose.ui.Alignment
    20|import androidx.compose.ui.Modifier
    21|import androidx.compose.ui.platform.LocalContext
    22|import androidx.compose.ui.text.font.FontWeight
    23|import androidx.compose.ui.unit.dp
    24|import androidx.compose.ui.viewinterop.AndroidView
    25|import androidx.lifecycle.viewmodel.compose.viewModel
    26|import org.osmdroid.config.Configuration
    27|import org.osmdroid.tileprovider.tilesource.TileSourceFactory
    28|import org.osmdroid.util.GeoPoint
    29|import org.osmdroid.views.MapView
    30|import org.osmdroid.views.overlay.Marker
import space.jtcao.visepanda.data.model.MapMarker
    31|import space.jtcao.visepanda.data.model.MapMarker
    32|
    33|/**
    34| * Full China map with 36 city markers using osmdroid.
    35| *
    36| * osmdroid uses OpenStreetMap data — works worldwide including China,
    37| * no API key required.
    38| */
    39|@Composable
    40|fun MapScreen(
    41|    onCityClick: (String) -> Unit,
    42|    viewModel: MapViewModel = viewModel()
    43|) {
    44|    val uiState by viewModel.uiState.collectAsState()
    45|    var selectedCity by remember { mutableStateOf<MapMarker?>(null) }
    46|
    47|    Box(modifier = Modifier.fillMaxSize()) {
    48|        when (val state = uiState) {
    49|            is MapUiState.Loading -> { /* Map will load shortly */ }
    50|            is MapUiState.Success -> {
    51|                OSMChinaMap(
    52|                    cities = state.cities,
    53|                    onMarkerClick = { city ->
    54|                        selectedCity = city
    55|                    },
    56|                    modifier = Modifier.fillMaxSize()
    57|                )
    58|
    59|                // City info popup
    60|                selectedCity?.let { city ->
    61|                    CityInfoPopup(
    62|                        city = city,
    63|                        onDismiss = { selectedCity = null },
    64|                        onViewDetail = {
    65|                            selectedCity = null
    66|                            onCityClick(city.name)
    67|                        },
    68|                        modifier = Modifier
    69|                            .align(Alignment.BottomCenter)
    70|                            .padding(16.dp)
    71|                    )
    72|                }
    73|            }
    74|            is MapUiState.Error -> {
    75|                Text(
    76|                    text = "Failed to load map data",
    77|                    style = MaterialTheme.typography.bodyLarge,
    78|                    color = MaterialTheme.colorScheme.error,
    79|                    modifier = Modifier.padding(16.dp)
    80|                )
    81|            }
    82|        }
    83|    }
    84|}
    85|
    86|/**
    87| * osmdroid MapView wrapped in Compose AndroidView.
    88| * Center: China (35.86, 104.19) at zoom 4 with 36 city markers.
    89| */
    90|@Composable
    91|private fun OSMChinaMap(
    92|    cities: List<MapMarker>,
    93|    onMarkerClick: (MapMarker) -> Unit,
    94|    modifier: Modifier = Modifier
    95|) {
    96|    val context = LocalContext.current
    97|
    98|    // Configure osmdroid once
    99|    remember {
   100|        Configuration.getInstance().apply {
   101|            userAgentValue = context.packageName
   102|            osmdroidBasePath = context.cacheDir
   103|            osmdroidTileCache = context.cacheDir.resolve("tiles")
   104|        }
   105|    }
   106|
   107|    var mapView by remember { mutableStateOf<MapView?>(null) }
   108|
   109|    AndroidView(
   110|        factory = { ctx ->
   111|            MapView(ctx).apply {
   112|                setTileSource(TileSourceFactory.MAPNIK)
   113|                setMultiTouchControls(true)
   114|                setBuiltInZoomControls(false)
   115|
   116|                // Center on China
   117|                controller.setZoom(4.0)
   118|                controller.setCenter(GeoPoint(35.86, 104.19))
   119|
   120|                // Add city markers
   121|                cities.forEach { city ->
   122|                    val marker = Marker(this).apply {
   123|                        position = GeoPoint(city.lat, city.lng)
   124|                        title = city.name
   125|                        snippet = city.vibe
   126|                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
   127|                        icon = createCityMarkerIcon(ctx)
   128|                        setOnMarkerClickListener { _, _ ->
   129|                            onMarkerClick(city)
   130|                            true
   131|                        }
   132|                    }
   133|                    overlays.add(marker)
   134|                }
   135|
   136|                mapView = this
   137|                invalidate()
   138|            }
   139|        },
   140|        modifier = modifier,
   141|        update = { view ->
   142|            // Update if needed (drag/zoom state, etc.)
   143|        }
   144|    )
   145|}
   146|
   147|/**
   148| * Create a simple colored circle marker for cities.
   149| */
   150|private fun createCityMarkerIcon(context: android.content.Context): Drawable? {
   151|    val size = (24 * context.resources.displayMetrics.density).toInt()
   152|    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
   153|    val canvas = android.graphics.Canvas(bitmap)
   154|    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
   155|        color = 0xFFE8912E.toInt() // PandaAmberDark
   156|        style = android.graphics.Paint.Style.FILL
   157|    }
   158|    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint)
   159|    // White border
   160|    paint.color = android.graphics.Color.WHITE
   161|    paint.style = android.graphics.Paint.Style.STROKE
   162|    paint.strokeWidth = 2f
   163|    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f - 1f, paint)
   164|    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
   165|}
   166|
   167|/**
   168| * Popup card when a city marker is tapped.
   169| */
   170|@Composable
   171|private fun CityInfoPopup(
   172|    city: MapMarker,
   173|    onDismiss: () -> Unit,
   174|    onViewDetail: () -> Unit,
   175|    modifier: Modifier = Modifier
   176|) {
   177|    Card(
   178|        onClick = onViewDetail,
   179|        modifier = modifier,
   180|        shape = RoundedCornerShape(16.dp),
   181|        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
   182|        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
   183|    ) {
   184|        Column(modifier = Modifier.padding(16.dp)) {
   185|            Text(
   186|                text = city.nameCn,
   187|                style = MaterialTheme.typography.headlineSmall,
   188|                fontWeight = FontWeight.Bold
   189|            )
   190|            Text(
   191|                text = city.name.replaceFirstChar { it.uppercase() },
   192|                style = MaterialTheme.typography.titleMedium,
   193|                color = MaterialTheme.colorScheme.onSurfaceVariant
   194|            )
   195|            if (city.vibe.isNotEmpty()) {
   196|                Text(
   197|                    text = city.vibe,
   198|                    style = MaterialTheme.typography.bodyMedium,
   199|                    modifier = Modifier.padding(top = 4.dp)
   200|                )
   201|            }
   202|            if (city.days.isNotEmpty()) {
   203|                Text(
   204|                    text = "⏱️ ${city.days}",
   205|                    style = MaterialTheme.typography.bodySmall,
   206|                    color = MaterialTheme.colorScheme.onSurfaceVariant,
   207|                    modifier = Modifier.padding(top = 2.dp)
   208|                )
   209|            }
   210|            Text(
   211|                text = "Tap to explore →",
   212|                style = MaterialTheme.typography.labelMedium,
   213|                color = MaterialTheme.colorScheme.primary,
   214|                modifier = Modifier.padding(top = 8.dp)
   215|            )
   216|        }
   217|    }
   218|}
   219|