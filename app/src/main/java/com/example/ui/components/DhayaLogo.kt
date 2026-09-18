package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DhayaGold
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.DhayaNavyLight

@Composable
fun DhayaLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    showContact: Boolean = true
) {
    val goldBrush = Brush.linearGradient(
        colors = listOf(
            DhayaGoldBright,
            DhayaGold,
            Color(0xFFE5A91A),
            DhayaGoldBright
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width = (size.value * 0.05f).dp.coerceAtLeast(2.dp),
                brush = goldBrush,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner delicate ring
        Canvas(modifier = Modifier.size(size * 0.92f)) {
            drawCircle(
                color = Color(0xFFF1C40F).copy(alpha = 0.5f),
                style = Stroke(width = 1.5f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = (size.value * 0.06f).dp)
        ) {
            // Upper Graphic: Monogram DM + tilted Smartphone with sparkles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Monogram DM Graphic
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "D",
                        fontSize = (size.value * 0.28f).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        color = DhayaGoldBright
                    )
                    Text(
                        text = "M",
                        fontSize = (size.value * 0.17f).sp,
                        fontWeight = FontWeight.Bold,
                        color = DhayaNavy,
                        modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width((size.value * 0.03f).dp))

                // Tilted Phone with sparkles
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.rotate(14f)
                ) {
                    Box(
                        modifier = Modifier
                            .width((size.value * 0.16f).dp)
                            .height((size.value * 0.25f).dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(DhayaNavyLight)
                            .border(1.dp, DhayaGoldBright, RoundedCornerShape(3.dp))
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = DhayaGoldBright,
                        modifier = Modifier
                            .size((size.value * 0.10f).dp)
                            .padding(bottom = 4.dp)
                    )
                }
            }

            // DHAYA Text
            Text(
                text = "DHAYA",
                fontSize = (size.value * 0.22f).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = DhayaGoldBright
            )

            // MOBILES flanked by lines
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width((size.value * 0.10f).dp)
                        .height(1.dp)
                        .background(DhayaGold)
                )
                Text(
                    text = " MOBILES ",
                    fontSize = (size.value * 0.11f).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DhayaNavy,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .width((size.value * 0.10f).dp)
                        .height(1.dp)
                        .background(DhayaGold)
                )
            }

            if (showContact && size >= 70.dp) {
                Spacer(modifier = Modifier.height(2.dp))
                // Phone Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F396B))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size((size.value * 0.08f).dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "7550281815",
                        fontSize = (size.value * 0.085f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
