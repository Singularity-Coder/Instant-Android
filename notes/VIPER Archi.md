# VIPER ARCHI NOTES

VIPER is a relatively new Architecture Pattern. VIPER stands for
V – View: Just shows stuff using the “Presenter” just like MVP.
I – Interactor: It’s like “Repository” in MVVM which makes API calls, DB calls, etc. It’s like the Data access layer.
P – Presenter: It’s the Commander. It tells all other layers what to do like click events, data fetching data, etc.
E – Entity: It’s just POJO.
R – Router: This is the only thing that is different from MVP. It navigates across screens via “Presenter”.

VIPER is just MVP Architecture but with a screen navigation aspect separated from it. At least that’s how I understood it. The “Model” in MVP is renamed as “Interactor” which is just like the “Repository” in MVVM Architecture. In the end, it’s all about separating the concerns and making the code as modular as possible for making it easy to test individual modules. VIPER also follows the Single-Responsibility principle more than MVP. So it is generally considered better than MVP.


