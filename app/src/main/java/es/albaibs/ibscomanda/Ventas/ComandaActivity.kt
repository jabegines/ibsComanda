package es.albaibs.ibscomanda.Ventas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.albaibs.ibscomanda.databinding.ComandaActivityBinding



class ComandaActivity: AppCompatActivity() {
    private lateinit var binding: ComandaActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }




}