# MVI ARCHI NOTES

MVVM stands for Model View ViewModel.

M – Model: It’s POJO here.
V – View: It’s used for ViewBinding, Event handling, etc.
VM – ViewModel: This is the unique part. It’s a special class that handles configuration changes. This gets its data from the Repository class which is a Data Access Layer. So ViewModel is the abstraction for Data Access Layer.

