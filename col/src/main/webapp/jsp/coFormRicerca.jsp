<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  <style>
    body {
      font-family: 'Poppins', sans-serif;
      max-width: 700px;
      margin: 40px auto;
      padding: 0 20px;
      background-color: #fafafa;
      color: #333;
    }
    h1 {
      text-align: center;
      font-weight: 600;
      margin-bottom: 40px;
      color: #222;
    }
    form {
      display: flex;
      margin-bottom: 30px;
      justify-content: center;
    }
    input[type="text"] {
      flex: 1;
      padding: 10px 15px;
      font-size: 16px;
      border: 1px solid #ccc;
      border-radius: 6px 0 0 6px;
      outline: none;
      transition: border-color 0.3s;
    }
    input[type="text"]:focus {
      border-color: #4caf50;
    }
    button {
      padding: 10px 20px;
      font-size: 16px;
      border: 1px solid #4caf50;
      background-color: #4caf50;
      color: white;
      cursor: pointer;
      border-radius: 0 6px 6px 0;
      transition: background-color 0.3s, border-color 0.3s;
    }
    button:hover {
      background-color: #45a049;
      border-color: #45a049;
    }
    ul.results {
      list-style: none;
      padding-left: 0;
      border-top: 1px solid #ddd;
      margin: 0;
    }
    ul.results li {
      padding: 20px 0;
      border-bottom: 1px solid #eee;
    }
    ul.results a {
      font-weight: 600;
      color: #1a73e8;
      text-decoration: none;
      font-size: 18px;
      display: block;
      margin-bottom: 6px;
    }
    ul.results a:hover {
      text-decoration: underline;
    }
    ul.results .snippet {
      font-size: 14px;
      color: #555;
      line-height: 1.4;
    }
    .hidden {
      display: none;
    }
  </style>

  <h1>Simple Search</h1>
  <form id="search-form" onsubmit="return false;">
    <input type="text" id="query" name="query" placeholder="Search..." aria-label="Search" />
    <button type="button" id="search-btn">Search</button>
  </form>

  <ul class="results" id="results">
    <li>
      <a href="https://templatemo.com/live/templatemo_564_plot_listing" target="_blank" rel="noopener">Plot Listing Template</a>
      <p class="snippet">A clean, modern free HTML template for listing plots and properties.</p>
    </li>
    <li>
      <a href="https://www.google.com" target="_blank" rel="noopener">Google Search</a>
      <p class="snippet">The most popular search engine on the web.</p>
    </li>
    <li>
      <a href="https://plotly.com/javascript/" target="_blank" rel="noopener">Plotly.js</a>
      <p class="snippet">A graphing library for JavaScript built on top of D3.js and stack.gl.</p>
    </li>
  </ul>

  <script>
    const searchBtn = document.getElementById('search-btn');
    const queryInput = document.getElementById('query');
    const results = document.querySelectorAll('#results li');

    function filterResults() {
      const query = queryInput.value.trim().toLowerCase();
      let anyVisible = false;
      results.forEach(li => {
        const text = li.textContent.toLowerCase();
        if (text.includes(query)) {
          li.classList.remove('hidden');
          anyVisible = true;
        } else {
          li.classList.add('hidden');
        }
      });
      if (!anyVisible) {
        if (!document.getElementById('no-results')) {
          const noRes = document.createElement('li');
          noRes.id = 'no-results';
          noRes.textContent = 'No results found.';
          document.getElementById('results').appendChild(noRes);
        }
      } else {
        const noRes = document.getElementById('no-results');
        if (noRes) noRes.remove();
      }
    }

    searchBtn.addEventListener('click', filterResults);

    queryInput.addEventListener('keyup', function(e) {
      if(e.key === "Enter") {
        filterResults();
      }
    });
  </script>
  <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>