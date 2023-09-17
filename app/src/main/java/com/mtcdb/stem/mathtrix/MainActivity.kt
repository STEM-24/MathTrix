package com.mtcdb.stem.mathtrix

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.mtcdb.stem.mathtrix.calculator.CalculatorOptionsFragment
import com.mtcdb.stem.mathtrix.dictionary.DictionaryDatabaseHelper
import com.mtcdb.stem.mathtrix.dictionary.DictionarySuggestionAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView
    private lateinit var termTextView: TextView
    private lateinit var termDefinitionTextView: TextView
    private lateinit var termExamplesListView: ListView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var databaseHelper: DictionaryDatabaseHelper
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DictionaryDatabaseHelper(this)
        insertTermsIntoDatabase()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchView = findViewById(R.id.searchView)
        searchView.isIconifiedByDefault = false
        searchView.queryHint = "Search terms here..."

        termTextView = findViewById(R.id.termTextView)
        termDefinitionTextView = findViewById(R.id.term_definition)
        termExamplesListView = findViewById(R.id.term_examples)

        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val swipeUp = findViewById<ImageView>(R.id.swipe_up)
        swipeUp.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        swipeUp.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                swipeUp.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                swipeUp.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            }
        }
        swipeUp.setOnLongClickListener {
            Toast.makeText(this, "Dictionary", Toast.LENGTH_SHORT).show()
            true
        }

        searchView.suggestionsAdapter = null
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update the cursor in the adapter as the user types
                val cursor = getSuggestionsCursor(newText)
                searchView.suggestionsAdapter = DictionarySuggestionAdapter(
                    this@MainActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    cursor,
                    arrayOf("term"),
                    intArrayOf(android.R.id.text1)
                )
                return true
            }
        })

        searchView.suggestionsAdapter = null

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                // Handle suggestion selection (if needed)
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter?.getItem(position) as Cursor
                val term = cursor.getString(cursor.getColumnIndexOrThrow("term"))
                val definition = cursor.getString(cursor.getColumnIndexOrThrow("definition"))
                val example = cursor.getString(cursor.getColumnIndexOrThrow("example")).split(";")

                termTextView.text = term
                termDefinitionTextView.text = definition
                val examplesAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    example
                )
                termExamplesListView.adapter = examplesAdapter
                searchView.clearFocus()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                swipeUp.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                return true
            }
        })

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_calculator -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CalculatorOptionsFragment())
                        .addToBackStack(null)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true

                }
            }
            false
        }

        toggleDarkMode()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        val iconMode = findViewById<ImageView>(R.id.iconMode)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            iconMode.setImageResource(R.drawable.baseline_light_mode_24)
        } else {
            iconMode.setImageResource(R.drawable.baseline_dark_mode_24)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun getSuggestionsCursor(query: String?): Cursor? {
        val db = DictionaryDatabaseHelper(this).readableDatabase
        val selection = "term LIKE ?"
        val selectionArgs = arrayOf("$query%")
        val columns = arrayOf("_id", "term", "definition", "example")

        return db.query(
            "dictionary_terms",
            columns,
            selection,
            selectionArgs,
            null,
            null,
            "term ASC"
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }

            R.id.action_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }

    private fun insertTermsIntoDatabase() {
        val db = databaseHelper.writableDatabase

        //checks if the terms are already inserted
        val isDatabasePopulated = checkIfDatabaseIsPopulated(db)

        if (!isDatabasePopulated) {
            db.beginTransaction()
            try {
                val termsToInsert = listOf(
                    Triple("Accounts Receivable", "The amount of money owed to a company by its customers for goods or services delivered.", "Invoice payments; Outstanding balances"),
                    Triple("Cash Flow", "The movement of money in and out of a company, including income, expenses, and investments.", "Operating cash flow; Positive cash flow"),
                    Triple("Gross Profit Margin", "The percentage of revenue that exceeds the cost of goods sold, indicating a company's profitability.", "Gross profit; Profit margin"),
                    Triple("Return on Investment (ROI)", "A measure of the profitability of an investment, calculated as the net gain or loss divided by the initial investment.", "ROI formula; Investment analysis"),
                    Triple("Market Share", "The portion of a market's total sales that a particular company or product captures.", "Market dominance; Competitive analysis"),
                    Triple("Business Plan", "A formal document outlining a company's goals, strategies, and financial projections for the future.", "Startup business plan; Business plan template")
                )

                val insertQuery = "INSERT INTO dictionary_terms (term, definition, example) VALUES (?, ?, ?)"
                val insertStatement = db.compileStatement(insertQuery)

                for (termData in termsToInsert) {
                    insertStatement.bindString(1, termData.first)
                    insertStatement.bindString(2, termData.second)
                    insertStatement.bindString(3, termData.third)
                    insertStatement.executeInsert()
                }

                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    private fun checkIfDatabaseIsPopulated(db: SQLiteDatabase): Boolean {
        val query = "SELECT COUNT(*) FROM dictionary_terms"
        val cursor = db.rawQuery(query, null)
        var isPopulated = false
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                isPopulated = count > 0
            }
            cursor.close()
        }
        return isPopulated
    }
}