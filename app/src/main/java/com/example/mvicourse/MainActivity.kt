package com.example.mvicourse

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mvicourse.api.AnimalService
import com.example.mvicourse.model.Animal
import com.example.mvicourse.ui.theme.MVICourseTheme
import com.example.mvicourse.view.MainIntent
import com.example.mvicourse.view.MainState
import com.example.mvicourse.view.MainViewModel
import com.example.mvicourse.view.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, MainViewModelFactory(AnimalService.api))[MainViewModel::class.java]
        val onButtonClick: () -> Unit = {
            lifecycleScope.launch {
                viewModel.userIntent.send(MainIntent.FetchAnimals)
            }
        }
        enableEdgeToEdge()
        setContent {
            MVICourseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel, onButtonClick, Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel, onButtonClick: () -> Unit, modifier: Modifier) {
    when(val state = viewModel.state.value) {
        is MainState.Animals -> AnimalsListScreen(state.animals, modifier)
        is MainState.Error -> {
            IdleScreen(onButtonClick, modifier)
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
        }
        is MainState.Idle -> IdleScreen(onButtonClick, modifier)
        is MainState.Loading -> LoadingScreen(modifier)
    }
}

@Composable
fun IdleScreen(onButtonClick: () -> Unit, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onButtonClick) {
            Text(text = "Fetch Animals")
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun AnimalsListScreen(animals: List<Animal>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(animals) { animal ->
            AnimalItem(animal)
            HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp)  )
        }

    }
}

@Composable
fun AnimalItem(animal: Animal) {
    Row(Modifier
        .fillMaxWidth()
        .height(100.dp)) {
        val url = AnimalService.BASE_URL + animal.image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder),
            contentDescription = animal.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(RoundedCornerShape(size = 4.dp)).size(100.dp),
        )
        Column(Modifier.fillMaxSize().padding(start = 4.dp)) {
            Text(text = animal.name, fontWeight = FontWeight.Bold)
            Text(text = animal.location)
        }
    }
}

@Composable
@Preview
fun ShowIdleScreenPreview() {
    IdleScreen(onButtonClick = {}, modifier = Modifier)
}
