package ru.viewer.expensesviewer.model.objects.settings;

public class CategoryEntity {
    private int categoryId;
    private String categoryName;
    private boolean categoryDefault;

    public CategoryEntity(int categoryId, String categoryName, boolean categoryDefault) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDefault = categoryDefault;
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryDefault=" + categoryDefault +
                '}';
    }

    @SuppressWarnings("unused")
    public int getCategoryId() {
        return categoryId;
    }
    @SuppressWarnings("unused")
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    @SuppressWarnings("unused")
    public String getCategoryName() {
        return categoryName;
    }
    @SuppressWarnings("unused")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @SuppressWarnings("unused")
    public boolean isCategoryDefault() {
        return categoryDefault;
    }
    @SuppressWarnings("unused")
    public void setCategoryDefault(boolean categoryDefault) {
        this.categoryDefault = categoryDefault;
    }
}
