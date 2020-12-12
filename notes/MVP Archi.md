# MVP ARCHI NOTES

MVP stands for Model View Presenter.

M – Model: This is the Data Access Layer. API calls, DB calls are made here. It does not access the “View” directly.
V – View: This handles Click Events, View Bindings, etc. It does not access the “Model” directly.
P – Presenter: The one that orchestrates everything and has handles on “Model” and “View”.