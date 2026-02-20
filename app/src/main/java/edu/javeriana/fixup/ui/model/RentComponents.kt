package edu.javeriana.fixup.ui.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.SoftFawn
import java.text.NumberFormat
import java.util.*

@Composable
fun RentHeader(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF5F5F5),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = GreyOlive
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Arriendos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBrown
                )
                Text(
                    text = "3 habitaciones ⋅ 2 baños ⋅ parqueadero",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreyOlive
                )
            }
        }
    }
}

@Composable
fun FilterControls(modifier: Modifier = Modifier, resultCount: Int) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton(text = "Filtro", icon = Icons.Outlined.Tune)
            FilterButton(text = "Clasificar", icon = Icons.Outlined.SwapVert)
        }
        Text(
            text = "$resultCount resultados",
            style = MaterialTheme.typography.bodySmall,
            color = GreyOlive
        )
    }
}

@Composable
private fun FilterButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = CharcoalBrown)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, color = CharcoalBrown)
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = CharcoalBrown)
        }
    }
}

@Composable
fun PropertyCard(
    property: PropertyModel,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                val pagerState = rememberPagerState(pageCount = { 3 })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(property.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.sala)
                    )
                }

                // Featured Badge
                if (property.isFeatured) {
                    Surface(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Destacado",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Favorite Icon
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd).size(36.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }

                // Pager Indicators
                Row(
                    Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { iteration ->
                        val color = if (iteration == 0) Color.White else Color.White.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(6.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBrown
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.StarBorder, contentDescription = null, modifier = Modifier.size(16.dp), tint = GreyOlive)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remodelado con nosotros",
                        style = MaterialTheme.typography.bodySmall,
                        color = GreyOlive
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PropertyFeatureItem(icon = Icons.Outlined.Bed, text = "${property.bedrooms} hab.")
                    PropertyFeatureItem(icon = Icons.Outlined.Bathtub, text = "${property.bathrooms} baños")
                    if (property.hasParking) {
                        PropertyFeatureItem(icon = Icons.Outlined.DirectionsCar, text = "Parqueadero")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currencyFormat.format(property.price)} $ /Mes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBrown
                    )
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Seleccionar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyFeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = GreyOlive)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = GreyOlive)
    }
}

@Composable
fun MapAreaPlaceholder(
    properties: List<PropertyModel>,
    selectedPropertyId: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E7FF))
    ) {
        Image(
            painter = painterResource(id = R.drawable.map_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.5f
        )
        
        // Simulating some price tags based on design
        PriceTag(price = 1500000.0, isSelected = false, modifier = Modifier.offset(x = 50.dp, y = 80.dp))
        PriceTag(price = 2100000.0, isSelected = false, modifier = Modifier.offset(x = 200.dp, y = 50.dp))
        PriceTag(price = 1500000.0, isSelected = false, modifier = Modifier.offset(x = 320.dp, y = 80.dp))
        PriceTag(price = 1200000.0, isSelected = true, modifier = Modifier.offset(x = 180.dp, y = 120.dp))
        PriceTag(price = 1400000.0, isSelected = false, modifier = Modifier.offset(x = 40.dp, y = 180.dp))
        PriceTag(price = 1200000.0, isSelected = false, modifier = Modifier.offset(x = 130.dp, y = 220.dp))
        PriceTag(price = 1300000.0, isSelected = false, modifier = Modifier.offset(x = 300.dp, y = 200.dp))
    }
}

@Composable
private fun PriceTag(price: Double, isSelected: Boolean, modifier: Modifier = Modifier) {
    val displayPrice = when {
        price >= 1000000 -> "$${(price / 1000000).format(1)}M"
        else -> "$${(price / 1000).toInt()}K"
    }
    Surface(
        modifier = modifier,
        color = if (isSelected) Color.Black else Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Text(
            text = displayPrice,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
